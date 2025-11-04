package Utils;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class AnswerPreloadPriority {
    private static final Set<Integer> priorityQuestionIds = new ConcurrentSkipListSet<>();

    public static void addPriorityQuestion(int questionId) {
        priorityQuestionIds.add(questionId);
    }

    public static void removePriorityQuestion(int questionId) {
        priorityQuestionIds.remove(questionId);
    }

    public static boolean isPriority(int questionId) {
        return priorityQuestionIds.contains(questionId);
    }
    
    public static void resetPriorityList() {
    	priorityQuestionIds.clear();;
    }
}
