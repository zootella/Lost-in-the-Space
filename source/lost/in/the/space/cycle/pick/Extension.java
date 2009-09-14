package lost.in.the.space.cycle.pick;

public class Extension {
	
	public static String type(String ext) {
		ext = ext.toLowerCase();
		
		if (ext.equals("mp3"))  return "audio";
		if (ext.equals("ogg"))  return "audio";
		if (ext.equals("fla"))  return "audio";
		if (ext.equals("flac")) return "audio";

		if (ext.equals("txt"))  return "document";
		if (ext.equals("pdf"))  return "document";
		if (ext.equals("pst"))  return "document";
		if (ext.equals("rtf"))  return "document";
		if (ext.equals("doc"))  return "document";
		if (ext.equals("docx")) return "document";
		if (ext.equals("xls"))  return "document";
		if (ext.equals("xlsx")) return "document";
		if (ext.equals("ppt"))  return "document";
		if (ext.equals("pptx")) return "document";

		if (ext.equals("jpg"))  return "image";
		if (ext.equals("jpeg")) return "image";
		if (ext.equals("png"))  return "image";
		if (ext.equals("gif"))  return "image";

		if (ext.equals("avi"))  return "video";
		if (ext.equals("mpg"))  return "video";
		if (ext.equals("mpeg")) return "video";
		if (ext.equals("flv"))  return "video";
		if (ext.equals("mov"))  return "video";
		if (ext.equals("m4v"))  return "video";

		return "";
	}

}
