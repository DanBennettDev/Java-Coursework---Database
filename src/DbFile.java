/**
 * DbFile handling for database
 * also defines file extensions etc. and so an empty DbFile instance can also be used
 * as information-giving object in other classes
 */


import java.io.*;
import java.nio.file.FileSystemException;
import java.util.List;

public class DbFile {

    private final char DELIMITER = 0x0241E;
    private final char ENDRECORD = 0x0000A;
    private final String EXTENSION = ".dbf";

    private String name;
    private String filepath;
    private String folderpath;
    private int lineCounter = 0;


    DbFile(String subfolder, String name) {
        this.name = name;
        if (!subfolder.endsWith("/")) {
            subfolder = subfolder + "/";
        }
        this.filepath = subfolder + name + EXTENSION;
        this.folderpath = subfolder;
    }

    public String getName() {
        return name;
    }

    // converts record to a delimited string for storage in file.
    private String recordToLine(DbRecord rec) {
        String key = ((Integer)rec.getKey()).toString();
        String line = key + DELIMITER + fieldStrClean((String) rec.getVal(0));
        int i=1;
        while (i < rec.size()) {
            line = line + DELIMITER + rec.getVal(i++);
        }
        return line + ENDRECORD;
    }

    // converts delimited string to record for loading to table
    private DbRecord lineToRecord(DbTable t, String line) {
        String[] fields = line.split("" + DELIMITER, t.getFieldCount()+1);
        int key = Integer.parseInt(fields[0]);
        if(key>=t.getKeyNoIncrement()){
            t.setKey(key+1);
        }
        DbRecord r = new DbRecord(key, t.getFields());
        for (int i = 0; i < t.getFieldCount(); i++) {
            r.set(i, fields[i+1]);
         }
        return r;
    }

    // cleans delimiters and endline indicator characters out of incoming string
    // so that it can be stored safely in delimited file format
    private String fieldStrClean(String field) {
        String clean = field;
        if (clean.contains("" + DELIMITER)) {
            clean = clean.replace("" + DELIMITER, " ");
        }
        if (clean.contains("" + ENDRECORD)) {
            clean = clean.replace("" + ENDRECORD, " ");
        }
        return clean;
    }

    // reads a table from file - structure and data
    // TODO: see note on readStructureFromFile
    public void readFromFile(DbTable t) {
        t.truncate();
        t.setKey(0);
        String readPath = new File("").getAbsolutePath().concat("/" + filepath);
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(readPath));
            BufferedReader fr = new BufferedReader(isr);
            this.lineCounter = 0;
            fr.lines().forEach(l -> processFileLines(t, l));

        } catch (FileNotFoundException e) {
            System.out.println("Cannot connect to file " + readPath);
        }
    }

    // reads a table from file, structure only (eg header only)
    // TODO: this replicates readFromFile too much.
    // ok for now, but on updating either method instead refactor both into 1 method with
    // option to load structure or full table, to avoid future duplication of effort
    public void readStructureFromFile(DbTable t) throws Exception {
        t.truncate();
        String readPath = new File("").getAbsolutePath().concat("/" + filepath);
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(readPath));
            BufferedReader fr = new BufferedReader(isr);
            String hdr = fr.readLine();
            this.lineCounter = 0;
            processFileLines(t, hdr);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot connect to file " + readPath);
        } catch (IOException io) {
            System.out.println("cannot write to file ./" + filepath);
            throw io;
        }
    }

    // helper method for reading data from file. Processes first line of doc as
    // header, the remainder as data. Allows simpler lambda syntax in calling methods
    // assumes lineCounter has been reset to zero before running.
    private void processFileLines(DbTable t, String line) {
        if (lineCounter++ == 0) {
            makeTable(line, t);
            return;
        }
        DbRecord r = lineToRecord(t, line);
        t.insertRecord(lineToRecord(t, line));
    }

    // check if table structure has been set up, if not, set up from header details
    private void makeTable(String line, DbTable t) {
        if (t.getFieldCount() > 0) {
            //table already initialized
            return;
        }
        String[] parts = line.split("" + DELIMITER);
        String[] fieldParts;
        String fieldName;
        DbTypeName type = DbTypeName.STRING;
        int size = 0;
        for (int i = 0; i < parts.length; i++) {
            fieldParts = parts[i].split("\\(");
            fieldName = fieldParts[0];

            if (fieldParts[1].startsWith("STR")) {
                type = DbTypeName.STRING;
                size = getStringSizeFromHdr(fieldParts[1]);
            }
            if (fieldParts[1].startsWith("INT")) {
                type = DbTypeName.INT;
                size = 0;
            }
            if (fieldParts[1].startsWith("FLT")) {
                type = DbTypeName.FLOAT;
                size = 0;
            }

            t.addField(fieldName, type, size);
        }
    }

    //gets size of an individual string field as int - expects to be passed
    // format ##) - eg 30) for a 30 character string
    private int getStringSizeFromHdr(String part) {
        int numEnd = part.length() - 1;
        String sizeStr = part.substring(3, numEnd);
        return Integer.parseInt(sizeStr);
    }

    public String getFileExtension() {
        return EXTENSION;
    }

    ;

    // writes table to file
    public void writeToFile(DbTable t) throws Exception {
        createFolderIfNotExist(folderpath);
        File f = new File(filepath);
        String data = makeHeader(t.getFields());

        for(DbRecord rec : t.getRecords()){
            data = data + recordToLine(rec);
        }
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(f));
            ow.write(data);
            ow.close();
        } catch (FileNotFoundException e) {
            System.out.println("cannot find file ./" + filepath);
            throw e;
        } catch (IOException io) {
            System.out.println("cannot write to file ./" + filepath);
            throw io;
        }
    }

    // checks if folder does not exist at given folder path. Attempts to create if it does not
    public void createFolderIfNotExist(String folderPath) throws FileSystemException {
        File dir = new File(folderPath);

        if (!dir.exists()) {
            try {
                boolean mkdir = dir.mkdir();
                if (!mkdir) {
                    throw new FileSystemException(folderPath);
                }
            } catch (Exception e) {
                System.out.println("Could Not Create Folder");
                throw e;
            }
        }
    }

    // creates header string for table. Single delimited line indicating fieldnames and types
    private String makeHeader(List<DbField> fields) {
        String header = "";
        DbField thisfield;
        for (int i = 0; i < fields.size(); i++) {
            thisfield = fields.get(i);
            header = header + thisfield.getName() + "(";
            switch (thisfield.getType()) {
                case STRING:
                    header = header + "STR" + Integer.toString(thisfield.getSize());
                    break;
                case INT:
                    header = header + "INT";
                    break;
                case FLOAT:
                    header = header + "FLT";
                    break;
                default:
                    System.out.println("invalid type in makeheader");
                    throw new IllegalArgumentException();
            }
            header = header + ")" + DELIMITER;
        }
        return header + ENDRECORD;
    }


    public static void tests() {
        Tester t = new Tester();
        System.out.println("Testing: DbFile - internal");
        Boolean check = false;
        char ENDRECORD = 0x0000A;

        DbTable tab = new DbTable("tester");
        tab.addField("f1", DbTypeName.STRING, 30);
        tab.addField("f2", DbTypeName.STRING, 30);
        tab.addField("f3", DbTypeName.STRING, 30);
        tab.addField("f4", DbTypeName.STRING, 30);

        DbRecord r = new DbRecord(1, tab.getFields(), "Beef", "cartesian", "3", "Smoggy");
        DbFile f = new DbFile("./", "test");

        t.is(f.recordToLine(r), "1␞Beef␞cartesian␞3␞Smoggy" + ENDRECORD, "recordToLine gives correct output");
        t.is(f.fieldStrClean("1␞Beef␞cartesian␞3␞Smoggy"), "1 Beef cartesian 3 Smoggy", "fieldStrToCSV cleans delimiters");
        r = f.lineToRecord(tab, "1␞Beef␞cartesian␞3␞Smoggy");
        t.is((String) r.getVal(0), "Beef", "lineToRecord gives correct output0");
        t.is((String) r.getVal(1), "cartesian", "lineToRecord gives correct output1");
        t.is((String) r.getVal(2), "3", "lineToRecord gives correct output2");
        t.is((String) r.getVal(3), "Smoggy", "lineToRecord gives correct output3");

        t.is(f.makeHeader(tab.getFields()), "f1(STR30)␞f2(STR30)␞f3(STR30)␞f4(STR30)␞" + ENDRECORD, "Makes header correctly");

        t.is(f.getStringSizeFromHdr("STR30)"), 30, "reads size from string details");

        t.results();

    }

}



