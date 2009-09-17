
#include <windows.h>
#include <atlstr.h>
#include <jni.h>
#include "Launch.h"

// Launch the program or show an error message to the user
int WINAPI WinMain(HINSTANCE instance, HINSTANCE previous, PSTR command, int show) {

	// Start Java and launch the program
	CString r = Launch();
	if (r != "") { // Something didn't work

		// Show the user a message, and open the default browser to java.com
		CString s;
		s += Name() + " runs on Java.\r\n";
		s += "Click OK to go to www.java.com, where you can get it.\r\n";
		s += "Afterwards, run " + Name() + " to try again.\r\n";
		s += "\r\n";
		s += "Details: " + r + ".";
		if (MessageBox(NULL, s, Name(), MB_ICONEXCLAMATION | MB_OKCANCEL) == IDOK)
			ShellExecute(NULL, NULL, "http://www.java.com/", "", "", SW_SHOWNORMAL);
	}
}

// Start Java and launch the program
// Returns error information
// On success, this thread is killed and this function never returns
CString Launch() {

	// Get settings from the command line, launch.txt, and the registry
	CLines arguments, settings;
	CString required, working, main, r;
	r = GetSettings(&arguments, &settings, &required, &working, &main);
	if (r != "") return r;
	CString found, home, library;
	r = ReadRegistry(&found, &home, &library); // Find Java in the registry
	if (r != "") return r;
	r = CheckVersion(found, required); // Make sure the version is high enough
	if (r != "") return r;

	// Prepare this process
	r = SetLocale(); // Set the thread locale
	if (r != "") return r;
	r = SetWorking(working); // Change the present working directory
	if (r != "") return r;
	r = SetPath(home); // Add Java to the path environment variable
	if (r != "") return r;

	// Start Java and run the program
	JavaCreateType JavaCreate;
	r = LoadJava(library, &JavaCreate);
	if (r != "") return r;
	JavaVM *machine = NULL;
	JNIEnv *e = NULL;
	r = StartJava(JavaCreate, &settings, &machine, &e);
	if (r != "") return r;
	jclass c = NULL;;
	jmethodID method = NULL;;
	r = GetMethod(e, main, &c, &method);
	if (r != "") return r;
	r = CallMethod(e, machine, c, method, &arguments);
	if (r != "") return r;

	// Execution never reaches here
	return "";
}

// Get the command line arguments Windows passed this running exe
// Look for the launch text file several places to get the lines inside and some values
// Returns error information, or blank on success
CString GetSettings(CLines *arguments, CLines *settings, CString *required, CString *working, CString *main) {

	// Read the command line arguments Windows passed this running exe
	for (int i = 1; i < __argc; i++) // Skip the first one, the path to the exe
		arguments->add(__argv[i]);

	// Look for the launch settings text file several places
	bool found = false;
	if (!found)
		found = settings->file(Path("launch.txt"));
	if (!found)
		found = settings->file(Path("lib\\launch.txt"));
	if (!found)
		found = settings->file(Path(Name() + ".app\\Contents\\Resources\\Java\\launch.txt"));
	if (!found)
		return "Unable to find launch.txt";

	// Get settings inside
	if (!settings->get("==Version==", required))
		return "launch.txt must contain ==Version== heading";
	if (!settings->get("==Working==", working))
		return "launch.txt must contain ==Working== heading";
	if (!settings->get("==Main==", main))
		return "launch.txt must contain ==Main== heading";
	return "";
}

// Takes a type of Java virtual machine to use, like "client", the default
// Reads the registry to find where Java is installed
// Makes found   like "1.6"
// Makes home    like "C:\Program Files\Java\jre1.6.0_06"
// Makes library like "C:\Program Files\Java\jre1.6.0_06\bin\client\jvm.dll"
// Returns error information, or blank on success
CString ReadRegistry(CString *found, CString *home, CString *library) {

	// Read text from the registry
	CString path = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
	if (!Registry(HKEY_LOCAL_MACHINE, path, "CurrentVersion", found))
		return "Java not found";
	path += "\\" + (*found);
	if (!Registry(HKEY_LOCAL_MACHINE, path, "JavaHome", home))
		return "Registry missing Java home";
	if (!Registry(HKEY_LOCAL_MACHINE, path, "RuntimeLib", library))
		return "Registry missing runtime library";
	return "";
}

// Takes a version found like "1.6" and one required like "1.5"
// Returns blank if found is the same or bigger than required
CString CheckVersion(CString found, CString required) {

	// Parse the individual version numbers
	int foundmajor, foundminor, requiredmajor, requiredminor;
	if (!Version(found, &foundmajor, &foundminor))
		return "Registry version parsed incorrectly";
	if (!Version(required, &requiredmajor, &requiredminor))
		return "Unable to parse launch.txt version";

	// Make sure found isn't too small
	if ((foundmajor < requiredmajor) || (foundmajor == requiredmajor && foundminor < requiredminor))
		return "Found Java " + found + "; " + Name() + " requires " + required + " or later";

	// Looks good
	return "";
}

// Set the thread locale the way Java wants
// Returns error information, or blank on success
CString SetLocale() {

	// Change from the user to the system default locale
	if (SetThreadLocale(GetSystemDefaultLCID()) == 0)
		return "SetThreadLocale error " + Numerals(GetLastError());
	return "";
}

// Takes a relative path like "" for here or "folder\\subfolder" down
// Sets the present working directory from where this exe is running
// Returns error information, or blank on success
CString SetWorking(CString working) {

	// Change the present working directory
	if (!SetCurrentDirectory(Path(working)))
		return "SetCurrentDirectory error " + Numerals(GetLastError());
	return "";
}

// Takes the Java home folder path, like "C:\Program Files\Java\jre1.6.0_06"
// Adds the bin folder in it to the start of the path environment variable
// Returns error information, or blank on success
CString SetPath(CString home) {

	// Get the current path, like "C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem"
	TCHAR bay[LINE_WIDTH];
	if (!GetEnvironmentVariable("PATH", bay, LINE_WIDTH))
		return "GetEnvironmentVariable error " + Numerals(GetLastError());
	CString before = bay;

	// Stick "C:\Program Files\Java\jre1.6.0_06\bin;" on the start
	CString after = home + "\\bin";
	if (before != "")
		after += ";" + before;

	// Set the new value, only changes it for this process
	if (!SetEnvironmentVariable("PATH", after))
		return "SetEnvironmentVariable error " + Numerals(GetLastError());
	return "";
}

// Takes the path to the Java DLL, like "C:\Program Files\Java\jre1.6.0_06\bin\client\jvm.dll"
// Loads it and gets the address of a function inside
// Returns error information, or blank on success
CString LoadJava(CString library, JavaCreateType *JavaCreate) {

	// Load the DLL
	HMODULE module = LoadLibrary(library);
	if (!module)
		return "LoadLibrary error " + Numerals(GetLastError());

	// Get pointers to two functions inside
	*JavaCreate = (JavaCreateType)GetProcAddress(module, "JNI_CreateJavaVM");
	if (!(*JavaCreate))
		return "GetProcAddress error " + Numerals(GetLastError());
	return "";
}

// Takes the create Java function pointer to use it
// Takes the list of settings that contains text arguments for Java
// Starts Java
// Saves the machine and e pointers
// Returns error information, or blank on success
CString StartJava(JavaCreateType JavaCreate, CLines *settings, JavaVM **machine, JNIEnv **e) {

	// Find the line index where the Java parameters begin
	int j = settings->find("==Java==");
	if (j == -1)
		return "launch.txt must contain ==Java== heading";
	j++; // Move past the Java heading to the first parameter
	int n = settings->size() - j; // How many parameters we have

	// Package each line of text in a JavaVMOption structure
	JavaVMOption option[LINE_HEIGHT];
	for (int i = 0; i < n; i++) {
		ZeroMemory(&(option[i]), sizeof(option[i]));
		option[i].optionString = settings->get(j + i); // Point at the character buffer in settings
		option[i].extraInfo = NULL;
	}

	// Prepare a structure of information for Java
	JavaVMInitArgs a;
	ZeroMemory(&a, sizeof(a));
	a.version = JNI_VERSION_1_2;
	a.ignoreUnrecognized = JNI_FALSE;
	a.nOptions = n; // How many JavaVMOption structures we're giving Java
	a.options = option; // Where the first one starts

	// Create the Java virtual machine
	jint result = JavaCreate(machine, (void **)e, &a);
	if (result != JNI_OK)
		return "Error " + Numerals(result) + " creating Java virtual machine";
	return "";
}

// Takes a class name with slashes, like "com/domain/program/Main"
// Points c at the class and method at the main method in it
// Returns error information, or blank on success
CString GetMethod(JNIEnv *e, CString name, jclass *c, jmethodID *method) {

	// Ask the Java environment to find the class
	*c = e->FindClass(name);
	if (!(*c))
		return "Unable to find main class";

	// Find the main method in it
	*method = e->GetStaticMethodID(*c, "main", "([Ljava/lang/String;)V");
	if (!(*method))
		return "Unable to find main method";
	return "";
}

// Takes the command line arguments Windows passed this running exe
// Runs the main method with those arguments
// Returns error information
// On success, this thread is killed and this function never returns
CString CallMethod(JNIEnv *e, JavaVM *machine, jclass c, jmethodID method, CLines *arguments) {

	// Package the Windows arguments into a Java string array
	jobjectArray a = JavaStringArray(e, arguments);
	if (!a)
		return "Problem creating Java string array";

	// Call the main method
	e->CallStaticVoidMethod(c, method, a);
	if (e->ExceptionOccurred())
		return "Calling main produced an exception";

	// Detach the current thread so the program closes correctly
	jint result = machine->DetachCurrentThread();
	if (result != JNI_OK)
		return "Problem detaching current thread";

	// This call kills this running native code, nothing after runs
	machine->DestroyJavaVM();
	return ""; // Execution never reaches here
}
