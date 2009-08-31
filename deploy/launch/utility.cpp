
#include <windows.h>
#include <atlstr.h>
#include <jni.h>
#include "Launch.h"

// Takes a root key handle and a key path
// Opens the key for reading
bool CRegistry::Open(HKEY root, CString path) {

	// Open the key
	HKEY key;
	int result = RegOpenKeyEx(
		root,     // Handle to root key
		path,     // Subkey name
		0,
		KEY_READ, // We only need to read the key we're opening
		&key);    // The opened key handle is put here
	if (result != ERROR_SUCCESS)
		return false;

	// Save the open key in this object
	Key = key;
	return true;
}

// Takes a root key handle, a key path, and a variable name
// Gets the text from the registry, setting value
bool Registry(HKEY root, CString path, CString name, CString *value) {

	// Open the key
	CRegistry registry;
	if (!registry.Open(root, path))
		return false;

	// Get the size required
	DWORD size;
	int result = RegQueryValueEx(
		registry.Key, // Handle to an open key
		name,         // Name of the value to read
		0,
		NULL,
		NULL,         // No data buffer, we're requesting the size
		&size);       // Required size in bytes including the null terminator
	if (result != ERROR_SUCCESS)
		return false;

	// Open a string
	CString s;
	LPTSTR buffer = s.GetBuffer(size / sizeof(TCHAR)); // How many characters we'll write, including the null terminator

	// Read the binary data
	result = RegQueryValueEx(
		registry.Key,   // Handle to an open key
		name,           // Name of the value to read
		0,
		NULL,
		(LPBYTE)buffer, // Data buffer, writes the null terminator
		&size);         // Size of data buffer in bytes
	s.ReleaseBuffer();
	if (result != ERROR_SUCCESS)
		return false;

	// Write the value and report success
	*value = s;
	return true;
}

// Takes a relative path like "" for here or "folder\subfolder" down
// Finds the folder we're running in and returns the path relative from it
CString Path(CString relative) {
	CString s = Running();
	s = Before(s, '\\'); // Chop off "\Program.exe" at the end
	if (relative != "")
		s += "\\" + relative; // Add on any given subfolders
	return s;
}

// The name of this program, from the file name of this running exe
CString Name() {
	CString s = Running();
	s = After(s, '\\');
	s = Before(s, '.');
	return s;
}

// The path to this running exe
CString Running() {
	TCHAR bay[MAX_PATH];
	GetModuleFileName(NULL, bay, MAX_PATH);
	return bay;
}

// The part of s that's before the last place c appears
CString Before(CString s, TCHAR c) {
	int i = s.ReverseFind(c);
	if (i == -1)
		return s; // Not found, it's all before
	return s.Left(i);
}

// The part of s that's after the last place c appears
CString After(CString s, TCHAR c) {
	int i = s.ReverseFind(c);
	if (i == -1)
		return ""; // Not found, nothing is after
	return s.Mid(i + 1);
}

// Convert a number like 42 into the numerals "42"
CString Numerals(int i) {
	TCHAR bay[MAX_PATH];
	itoa(i, bay, 10); // Base 10
	return bay;
}

// Takes version number text like "1.2"
// Parses it into major 1 and minor 2
bool Version(CString s, int *major, int *minor) {

	// Find the decimal point
	int i = s.Find('.');
	if (i == -1)
		return false; // Not found

	// Pull out the numbers before and after
	*major = atoi(s.Left(i));
	*minor = atoi(s.Mid(i + 1));
	return true;
}

// Make a Java string array
jobjectArray JavaStringArray(JNIEnv *e, CLines *lines) {

	jclass c = e->FindClass("java/lang/String");
	if (!c)
		return NULL;
	jobjectArray a = e->NewObjectArray(lines->size(), c, 0);
	if (!c)
		return NULL;

	for (int i = 0; i < lines->size(); i++) {

		jstring s = JavaString(e, lines->get(i));
		if (!s)
			return NULL;

		e->SetObjectArrayElement(a, i, s);
		e->DeleteLocalRef(s);
	}

	return a;
}

// Make a Java string
jstring JavaString(JNIEnv *e, char *s) {

	jclass c = e->FindClass("java/lang/String");
	if (!c)
		return NULL;
	jmethodID m = e->GetMethodID(c, "<init>", "([B)V");
	if (!m)
		return NULL;

	int length = lstrlen(s);
	jbyteArray a = e->NewByteArray(length);
	if (!a)
		return NULL;
	e->SetByteArrayRegion(a, 0, length, (jbyte *)s);
	if (e->ExceptionOccurred())
		return NULL;

	jstring j = (jstring)e->NewObject(c, m, a);
	if (!j)
		return NULL;

	e->DeleteLocalRef(a);
	return j;
}
