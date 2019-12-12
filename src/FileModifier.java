import java.io.*;
import java.util.*;

public class FileModifier {


    public static String readFromFile(String filepath) throws IOException {
        // Method reads all the lines from a given file and returns all these lines as a string.
        // Input: String filepath - File's path that is being read as a string.
        // Output: String lines - All the lines from given file.

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "windows-1252"))) {
            StringBuilder lines = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
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

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "windows-1252"))) {
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
        for (int col : cols) {
            unwantedColumns.add(col);
        }

        StringBuilder newText = new StringBuilder();

        String text = readFromFile(filename);
        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {

            String[] columns = lines[i].split(",");

            for (Integer unwantedColumn : unwantedColumns)
                columns[unwantedColumn] = null;

            for (String column : columns)
                if (column != null) {
                    newText.append(column);
                    newText.append(",");
                }

            newText.deleteCharAt(newText.length() - 1);
            newText.append("\n");

        }
        writeToFile(newText.toString(), filename);

    }

    public static void createDuplicateFromFile(String existingFileName, String newFilePath, String newFileName) throws IOException {
        // Duplicates existing file.
        // Input: String existingFileName - File that is being duplicated.
        //        String newFileName - Filename of the duplicate that is being created.
        // Output: Method creates a new file, that is the copy of an existing file.

        String text = readFromFile(existingFileName);
        File dirs = new File(newFilePath);
        dirs.mkdirs();
        File file = new File(newFilePath + newFileName);
        file.createNewFile();
        writeToFile(text, newFilePath + newFileName);
    }

    public static void eraseDuplicates(String filename) throws IOException {
        // Method erases duplicate lines from file.
        // Input: String filename - name of the file being processed.
        // Output: File gets refactored without duplicate lines.

        String text = readFromFile(filename);
        String[] lines = text.split("\n");
        Set<String> nameArr = new HashSet<>();

        for (int i = 0; i < lines.length; i++) {
            String name = lines[i].split(",")[0];
            if (nameArr.contains(name))
                lines[i] = null;
            else
                nameArr.add(name);
        }

        StringBuilder newText = new StringBuilder();
        for (String line : lines) {
            if (line != null) {
                newText.append(line);
                newText.append("\n");
            }
        }

        writeToFile(newText.toString(), filename);

    }

    public static ArrayList<String> getColumn(String filename, int columnNumber, boolean discardHeader) throws IOException {
        // Method processes a specified file and pairs one column, containing keys, to another, containing values.
        // Input: int keyColNumber - column that contains key values.
        //        int valueColNumber - column that contains value values.
        //        String filename - file that contains columns of data (.csv files)
        // Output: Map<String,String> keyValue - returns map that has keys from one column and corresponding values from
        //                                       another column.

        ArrayList<String> columnValues = new ArrayList<>();

        String text = readFromFile(filename);
        String[] lines = text.split("\n");
        for (String line : lines) {
            String columnValue = line.split(",")[columnNumber];
            columnValues.add(columnValue);
        }

        if (discardHeader)
            columnValues.remove(0);

        return columnValues;

    }

    public static String linesToText(String[] lines) {
        // Method converts array of String to text, where each element is separated by a linebreak '\n'.
        // Input: String[] lines - array, that will be converted into a text (String)
        // Output: String - text from lines.
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            text.append(line);
            text.append("\n");
        }
        return text.toString();

    }

    public static String doubleCheckName(String name, String[] allNames) {
        // Method checks if given name is in the list of all names, first by first name and then by last name.
        // Input: String name - the name that is being searched from list
        //        String[] allNames - list of names from where the given name is searched
        // Output: String - if player was found, then returns the same name that was given, otherwise returns null
        ArrayList<String> candidateNames = new ArrayList<>();

        String[] names = name.split(",");
        for (String playerName : allNames) {
            if (playerName.contains(names[0])) {
                candidateNames.add(playerName);
            }
        }

        for (String playerName : candidateNames) {
            if (playerName.contains(names[names.length - 1])) {
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

            lines[i] = (names.indexOf(name) + 649) + "," + lines[i];
        }

        writeToFile(linesToText(lines), filename);
        //System.out.println(names.size());
    }

    private static void removeColumnsFromFile(String filepath, Integer[] unnecessaryCols) throws IOException {
        String[] rows = rowsFromText(readFromFile(filepath));
        String[] purgedRows = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            purgedRows[i] = purgeRow(rows[i], unnecessaryCols);
            //System.out.println(purgedRows[i]);
        }

        writeToFile(linesToText(purgedRows), filepath);

    }

    private static String purgeRow(String row, Integer[] unnecessaryCols) {
        Arrays.sort(unnecessaryCols, Collections.reverseOrder());
        ArrayList values = new ArrayList<>(Arrays.asList(row.split(",")));
        for (int column : unnecessaryCols)
            values.remove(column);
        return valuesToString(values);

    }

    private static String valuesToString(Iterable values) {
        StringBuilder string = new StringBuilder();
        for (Object value : values)
            string.append(value.toString()).append(",");
        return string.deleteCharAt(string.length() - 1).toString();
    }

    private static String nameAndValuesToString(ArrayList<Integer[]> valuematrix, ArrayList<String> names) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < valuematrix.size(); i++) {
            string.append(names.get(i)).append(",");
            for (int j = 0; j < valuematrix.get(i).length; j++) {
                if (j != 0)
                    string.append(valuematrix.get(i)[j]).append(",");
            }
            string.deleteCharAt(string.length() - 1);
            string.append("\n");
        }
        return string.delete(string.length() - 1, string.length()).toString();
    }

    private static void mergeEveryTeamToOneRow(String filepath) throws IOException {
        String[] rows = rowsFromText(readFromFile(filepath));
        String[] columnNames = rows[0].split(",");
        int opponentIndex = -1;
        int fixtureId = -1;
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals("opponent_team"))
                opponentIndex = i;
            else if (columnNames[i].equals("fixture"))
                fixtureId = i;
        }
        ArrayList<Integer[]> newRows = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (int j = 1; j < rows.length; j++) {
            String[] rowvalues = rows[j].split(",");
            if (!newRows.isEmpty()) {
                int opponent = Integer.parseInt(rowvalues[opponentIndex]);
                int fixture = Integer.parseInt(rowvalues[fixtureId]);
                boolean hasteam = false;
                for (Integer[] newRow : newRows) {
                    if (newRow[opponentIndex] == opponent && newRow[fixtureId] == fixture) {
                        hasteam = true;
                        for (int i = 0; i < rowvalues.length; i++) {
                            if (!columnNames[i].equals("name") && !columnNames[i].equals("element") && !columnNames[i].equals("fixture") &&
                                    !columnNames[i].equals("minutes") && !columnNames[i].equals("opponent_team") && !columnNames[i].equals("round") &&
                                    !columnNames[i].equals("team_a_score") && !columnNames[i].equals("team_h_score") && !columnNames[i].equals("was_home"))
                                newRow[i] += Integer.parseInt(rowvalues[i]);
                        }
                        break;
                    }
                }
                if (!hasteam) {
                    newRows.add(new Integer[columnNames.length]);
                    newRows.get(newRows.size() - 1)[0] = 0;
                    names.add(rowvalues[0]);
                    for (int i = 1; i < columnNames.length; i++) {
                        try {
                            newRows.get(newRows.size() - 1)[i] = Integer.parseInt(rowvalues[i]);
                        } catch (NumberFormatException e) {
                            if (rowvalues[i].equals("False"))
                                newRows.get(newRows.size() - 1)[i] = 0;
                            else if (rowvalues[i].equals("True"))
                                newRows.get(newRows.size() - 1)[i] = 1;
                            else
                                throw e;
                        }
                    }
                }
            } else {
                newRows.add(new Integer[columnNames.length]);
                newRows.get(newRows.size() - 1)[0] = 0;
                names.add(rowvalues[0]);
                for (int i = 1; i < columnNames.length; i++) {
                    try {
                        newRows.get(newRows.size() - 1)[i] = Integer.parseInt(rowvalues[i]);
                    } catch (NumberFormatException e) {
                        if (rowvalues[i].equals("False"))
                            newRows.get(newRows.size() - 1)[i] = 0;
                        else if (rowvalues[i].equals("True"))
                            newRows.get(newRows.size() - 1)[i] = 1;
                        else
                            throw e;
                    }
                }
            }
        }
        writeToFile(rows[0] + "\n" + nameAndValuesToString(newRows, names), filepath);
    }

    public static void main(String[] args) throws IOException {

        // All paths to gw files
        List<String> filepaths;
        for (int j = 6; j < 9; j++) {
            filepaths = new ArrayList<>();
            for (int i = 1; i < 39; i++)
                filepaths.add("201" + j + "-1" + (j + 1) + "/gws/gw" + i + ".csv");

            // Convenience table that maps column name to column index.
            Map<String, Integer> columnIndexes = getColumnNamesToIndexesTable(filepaths.get(0));

            // Create duplicate files and erase the unneccessary columns from duplicates.
            for (String filepath : filepaths) {
                String newPath = "Parsed_201" + j + "_1" + (j + 1) + "/gws/";
                String name = filepath.split("/")[2];
                createDuplicateFromFile(filepath, newPath, name);
                removeColumnsFromFile(newPath + name, new Integer[]{
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
                        columnIndexes.get("id"),
                        columnIndexes.get("element"),
                        columnIndexes.get("ea_index"),
                        columnIndexes.get("creativity"),
                        columnIndexes.get("clean_sheets"),
                        columnIndexes.get("bps"),
                        columnIndexes.get("bonus"),
                        columnIndexes.get("assists")
                });
                String newGamePath = "Parsed_201" + j + "_1" + (j + 1) + "/gwgames/";
                createDuplicateFromFile(newPath + name, newGamePath, name);
                mergeEveryTeamToOneRow(newGamePath + name);
                addColumnToFile(newGamePath + name, createShotsOnTargetColumn(newGamePath + name));
            }
        }
    }

    private static void addColumnToFile(String newGamePath, List<String> shotsOnTargetColumn) throws IOException {
        String[] lines = readFromFile(newGamePath).split("\n");
        for (int i = 0; i < lines.length; i++)
            lines[i] += "," + shotsOnTargetColumn.get(i);
        writeToFile(linesToText(lines), newGamePath);
    }

    private static List<String> createShotsOnTargetColumn(String filepath) throws IOException {
        // Method creates a List shotsOnTarget, that should be interpreted as a column

        Map<String, Integer> columnIndexes = getColumnNamesToIndexesTable(filepath);

        // Create a map that stores club's shots and keys queue that keeps track which club came first in the file, which club came second etc;
        Map<String, String> clubsShots = new HashMap<>();
        Queue<String> keys = new LinkedList<>();

        for (String line : readFromFile(filepath).split("\n")) {
            String[] values = line.split(",");

            try {
                // Generating an unique key from fixture and was_home column. Key1 is used to get the team's shots from Map (clubsShots) in chronological order
                // (the team that came first in the file is the team whose shots are taken first from the map).
                String key1 = Integer.parseInt(values[columnIndexes.get("was_home")]) + values[columnIndexes.get("fixture")];
                //System.out.println("Key1 = " + key1);
                keys.add(key1);

                // Generating an unique key from fixture and !was_home column. Key2 is the key to the opponent_team that had shots on target.
                String key2 = Math.abs(Integer.parseInt(values[columnIndexes.get("was_home")]) - 1) + values[columnIndexes.get("fixture")];
                //System.out.println("Key2 = " + key2 + "\n");
                String shotsOnTarget = values[columnIndexes.get("saves")];
                clubsShots.put(key2, shotsOnTarget);
            } catch (NumberFormatException e) {
                System.out.println("Header row : " + line);
            }
        }

        ArrayList<String> shotsOnTarget = new ArrayList<>();
        shotsOnTarget.add("shots_on_target");
        while (!keys.isEmpty()) {
            shotsOnTarget.add(clubsShots.get(keys.poll()));
        }

        return shotsOnTarget;
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















