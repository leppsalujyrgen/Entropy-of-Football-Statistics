import java.io.*;
import java.util.*;

public class FileModifier {


    public static String readFromFile(String filepath) throws IOException {
        // Method reads all the lines from a given file and returns all these lines as a string.
        // Input: String filepath - File's path that is being read as a string.
        // Output: String lines - All the lines from given file.

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "windows-1252"))) {
            StringBuilder lines = new StringBuilder();
            String line;

            while((line = br.readLine()) != null){
                lines.append(line);
                lines.append("\n");
            }
            return lines.toString();

        }

    }

    public static void writeToFile(String lines, String filename) throws IOException {
        // Method writes to file.
        // Input: String lines - lines that will be written into the file.
        //        String filename - name of the file, that gets written inside.
        // Output: Creates a new file (to path) and writes specified lines in it.

        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "windows-1252"))) {
            bw.write(lines);
        }

    }

    public static String[] rowsFromText(String text) {
        return text.split("\n");
    }

    public static void eraseColumns(int[] cols, String filename) throws IOException {
        // Method erases columns from file.
        // Input: int[] columns - contains numbers, for columns, that need to be erased.
        //        String filename - name of the file, that will be purged of unwanted columns
        // Output: Specified file (parameter filename) will be rewritten without unwanted columns.

        List<Integer> unwantedColumns = new ArrayList<>();
        for(int col : cols) {
            unwantedColumns.add(col);
        }

        StringBuilder newText = new StringBuilder();

        String text = readFromFile(filename);
        String[] lines = text.split("\n");

        for(int i = 0; i < lines.length; i++) {

            String[] columns = lines[i].split(",");

            for(Integer unwantedColumn : unwantedColumns)
                columns[unwantedColumn] = null;

            for (String column : columns)
                if(column != null) {
                    newText.append(column);
                    newText.append(",");
                }

            newText.deleteCharAt(newText.length() - 1);
            newText.append("\n");

        }
        writeToFile(newText.toString(), filename);

    }

    public static void createDuplicateFromFile(String existingFileName, String newFileName) throws IOException {
        // Duplicates existing file.
        // Input: String existingFileName - File that is being duplicated.
        //        String newFileName - Filename of the duplicate that is being created.
        // Output: Method creates a new file, that is the copy of an existing file.

        String text = readFromFile(existingFileName);
        writeToFile(text, newFileName);
    }

    public static void eraseDuplicates(String filename) throws IOException {
        // Method erases duplicate lines from file.
        // Input: String filename - name of the file being processed.
        // Output: File gets refactored without duplicate lines.

        String text = readFromFile(filename);
        String[] lines = text.split("\n");
        Set<String> nameArr = new HashSet<>();

        for(int i = 0; i < lines.length; i++) {
            String name = lines[i].split(",")[0];
            if(nameArr.contains(name))
                lines[i] = null;
            else
                nameArr.add(name);
        }

        StringBuilder newText = new StringBuilder();
        for(String line : lines) {
            if(line != null) {
                newText.append(line);
                newText.append("\n");
            }
        }

        writeToFile(newText.toString(), filename);

    }

    public static ArrayList<String> getColumn(String filename, int columnNumber) throws IOException {
        // Method processes a specified file and pairs one column, containing keys, to another, containing values.
        // Input: int keyColNumber - column that contains key values.
        //        int valueColNumber - column that contains value values.
        //        String filename - file that contains columns of data (.csv files)
        // Output: Map<String,String> keyValue - returns map that has keys from one column and corresponding values from
        //                                       another column.

        ArrayList<String> columnValues = new ArrayList<>();

        String text = readFromFile(filename);
        String[] lines = text.split("\n");
        for(String line : lines) {
            String columnValue = line.split(",")[columnNumber];
            columnValues.add(columnValue);
        }

        return columnValues;

    }

    public static String linesToText(String[] lines) {
        // Method converts array of String to text, where each element is separated by a linebreak '\n'.
        // Input: String[] lines - array, that will be converted into a text (String)
        // Output: String - text from lines.
        StringBuilder text = new StringBuilder();
        for(String line : lines) {
            text.append(line);
            text.append("\n");
        }
        return text.toString();

    }

    public static String doubleCheckName(String name, String[] allNames)  {
        ArrayList<String> candidateNames = new ArrayList<>();

        String[] names = name.split(",");
        for(String playerName : allNames) {
            if(playerName.contains(names[0])){
                candidateNames.add(playerName);
            }
        }

        for(String playerName : candidateNames) {
            if(playerName.contains(names[names.length - 1])){
                return playerName;
            }
        }

        return null;
    }

    public static void addIndexColumn(String filename) throws IOException {
        String text = readFromFile(filename);
        String[] lines = text.split("\n");

        ArrayList<String> names = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String name = lines[i].split(",")[0];

            if (!names.contains(name))
                names.add(name);

            lines[i] = (names.indexOf(name)+649) + "," + lines[i];
        }

        writeToFile(linesToText(lines), filename);
        System.out.println(names.size());
    }

    private static void removeColumnsFromFile(String filepath, int[] unnecessaryCols) throws IOException {
        String[] rows = rowsFromText(readFromFile(filepath));
        String[] purgedRows = new String[rows.length];
        for (int i = 0; i < rows.length; i++)
            purgedRows[i] = purgeRow(rows[i], unnecessaryCols);

        writeToFile(linesToText(purgedRows), filepath);

    }

    private static String purgeRow(String row, int[] unnecessaryCols) {
        ArrayList<String> values = new ArrayList<>(Arrays.asList(row.split(",")));
        for (int column : unnecessaryCols)
            values.remove(column);
        return valuesToString(values);

    }

    private static String valuesToString(Iterable values) {
        StringBuilder string = new StringBuilder();
        for (Object value : values)
            string.append(value.toString()).append(",");
        return string.deleteCharAt(string.length()-1).toString();
    }

    public static void main(String[] args) throws IOException {

        // All paths to gw files from the season 2016/17
        List<String> filepaths = new ArrayList<>();
        for (int i = 1; i < 39; i++)
            filepaths.add("2016-17/gws/gw" + i + ".csv");

        // Convenience table that maps column name to column index.
        Map<String, Integer> columnIndexes = getColumnNamesToIndexesTable(filepaths.get(0));

        // Create duplicate files and erase the unneccessary columns from duplicates.
        for (String filepath: filepaths) {
            String newPath = "Parsed_2016_17/" + filepath.split("/")[2];
            createDuplicateFromFile(filepath, newPath);
            removeColumnsFromFile(newPath, new int[]{
                    columnIndexes.get("winning_goals"),
                    columnIndexes.get("value"),
                    columnIndexes.get("transfers_balance"),
                    columnIndexes.get("transfers_out"),
                    columnIndexes.get("transfers_in"),
                    columnIndexes.get("total_points"),
                    columnIndexes.get("threat"),
                    columnIndexes.get("selected"),
                    columnIndexes.get("own_goals"),
                    columnIndexes.get("loaned_out"),
                    columnIndexes.get("loaned_in"),
                    columnIndexes.get("kickoff_time_formatted"),
                    columnIndexes.get("kickoff_time"),
                    columnIndexes.get("influence"),
                    columnIndexes.get("ict_index"),
                    columnIndexes.get("goals_scored"),
                    columnIndexes.get("goals_conceded"),
                    columnIndexes.get("errors_leading_to_goal"),
                    columnIndexes.get("element"),
                    columnIndexes.get("ea_index"),
                    columnIndexes.get("creativity"),
                    columnIndexes.get("clean_sheets"),
                    columnIndexes.get("bps"),
                    columnIndexes.get("bonus"),
                    columnIndexes.get("assists")
            });
        }



    }

    private static Map<String, Integer> getColumnNamesToIndexesTable(String filepath) throws IOException {
        String[] columnNames;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "windows-1252"))) {
            columnNames = br.readLine().replace("\n", "").split(",");
        }

        Map<String, Integer> columnIndexes = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++)
            columnIndexes.put(columnNames[i], i);

        return columnIndexes;
    }
}















