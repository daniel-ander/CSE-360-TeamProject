package DisplayModels;

import java.util.*;

public class AnswerNode {
    int id;
    Set<AnswerNode> children = new HashSet<>();
    Integer depth = null;  
    Integer parentAnswerID;

    public AnswerNode(int id, Integer parentAnswerID) {
        this.id = id;
        this.parentAnswerID = parentAnswerID;
    }
}