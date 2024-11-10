package com.example.flashcard.utils;

import com.example.flashcard.models.Quizz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Util {

    // Convert List<Quizz> to HashMap<String, Quizz>
    public HashMap<String, Quizz> convertListQuizzToHashMap(List<Quizz> quizzList) {
        HashMap<String, Quizz> m = new HashMap<>();
        for (Quizz q : quizzList) {
            m.put(q.getCategory(), q);
        }
        return m;
    }

    // Convert HashMap<String, Quizz> to List<Quizz>
    public List<Quizz> convertHashMapToListQuizz(HashMap<String, Quizz> quizzMap) {
        return new ArrayList<>(quizzMap.values());
    }
}
