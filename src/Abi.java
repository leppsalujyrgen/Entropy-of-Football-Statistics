import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Abi {
    public static void main(String[] args) {
        String s = "assists,bonus,bps,clean_sheets,creativity,goals_conceded,goals_scored,ea_index,id,errors_leading_to_goal,ict_index,influence,kickoff_time,kickoff_time_formatted,loaned_in,loaned_out,own_goals,selected,threat,total_points,transfers_in,transfers_out,transfer_balance,value,winning_goal";
        List<String> values = new ArrayList<>(Arrays.asList(s.split(",")));
        Collections.reverse(values);

        for (String value : values) {
            System.out.println("columnIndexes.get(\"" + value.strip() + "\"),");
        }
    }
}
