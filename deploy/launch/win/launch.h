
// A list of lines of text
#define LINE_WIDTH 4096 
#define LINE_HEIGHT 128
class CLines {
private:

	// Members
	char *line[LINE_HEIGHT]; // An array of pointers to lines of text
	int n;                   // The number of lines we have now

public:

	// Make a new CLines object to hold lines of text
	CLines() {
		n = 0; // We aren't holding any lines yet
	}

	// Delete this CLines object
	~CLines() {
		for (int i = 0; i < n; i++) // Loop for each line
			free(line[i]);          // Free the line
	}

	// Add a new line to this CLines object
	void add(CString s) {
		if (n == LINE_HEIGHT) // Make sure we're not full
			return;
		char *newline = (char *)malloc(lstrlen(s) + 1); // Add 1 for the terminator
		lstrcpy(newline, s); // Write the text and terminator
		line[n] = newline;   // Write the memory address
		n++;                 // Record we've got one more line
	}

	// Takes a path to a text file
	// Opens it and adds each line inside
	bool file(CString path) {

		// Open the file
		FILE *file = fopen(path, "r"); // Just for reading
		if (!file)
			return false;

		// Get each line, trim it, and add it
		char bay[LINE_WIDTH];
		while (fgets(bay, LINE_WIDTH - 1, file))
			add(CString(bay).Trim());

		// Close the file and report success
		fclose(file);
		return true;
	}

	// Get the text in line index i, 0 is the first one
	char *get(int i) {
		if (i < 0 || i >= n) // Check bounds
			return NULL;
		return line[i];
	}

	// Find out how many lines we have
	int size() {
		return n;
	}

	// Find the first index where the given line appears, -1 not found
	int find(char *c) {
		for (int i = 0; i < n; i++)       // Loop for each line
			if (lstrcmp(line[i], c) == 0) // It matches
				return i;                 // Return the index
		return -1; // Not found
	}

	// Get the value line after the given key line
	// Return false if not found
	bool get(char *key, CString *value) {
		int i = find(key);
		if (i == -1)
			return false; // Key line not found
		char *v = get(i + 1); // Value is next line
		if (!v)
			return false; // Value line out of bounds
		*value = v; // Save and report success
		return true;
	}
};

// Wraps a registry key, taking care of closing it
class CRegistry {
public:

	// The handle to the registry key
	HKEY Key;

	// Open a registry key and store its handle in this object
	bool Open(HKEY root, CString path);
	void Close() { if (Key) RegCloseKey(Key); Key = NULL; }

	// Make a new local CRegistry object, and delete it when it goes out of scope
	CRegistry() { Key = NULL; }
	~CRegistry() { Close(); }
};

// Function pointers
typedef jint (JNICALL *JavaCreateType)(JavaVM **pvm, void **env, void *args);

// Launch functions
int WINAPI WinMain(HINSTANCE instance, HINSTANCE previous, PSTR command, int show);
CString Launch();
CString GetSettings(CLines *arguments, CLines *settings, CString *required, CString *working, CString *main);
CString ReadRegistry(CString *found, CString *home, CString *library);
CString CheckVersion(CString found, CString required);
CString SetLocale();
CString SetWorking(CString working);
CString SetPath(CString home);
CString LoadJava(CString library, JavaCreateType *JavaCreate);
CString StartJava(JavaCreateType JavaCreate, CLines *settings, JavaVM **machine, JNIEnv **e);
CString GetMethod(JNIEnv *e, CString name, jclass *c, jmethodID *method);
CString CallMethod(JNIEnv *e, JavaVM *machine, jclass c, jmethodID method, CLines *arguments);

// Utility functions
bool Registry(HKEY root, CString path, CString name, CString *value);
CString Path(CString relative);
CString Name();
CString Running();
CString Before(CString s, TCHAR c);
CString After(CString s, TCHAR c);
CString Numerals(int i);
bool Version(CString s, int *major, int *minor);
jobjectArray JavaStringArray(JNIEnv *e, CLines *lines);
jstring JavaString(JNIEnv *e, char *s);
