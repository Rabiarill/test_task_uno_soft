package ru.rabiarill;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

   public static void main(String[] args) {
      long startTime = System.currentTimeMillis();
      String filePath = args[0];
      List<String> lines = readFile(filePath);

      Set<Set<String>> mergedGroups = getGroups(lines);

      writeFile(mergedGroups, filePath);

      long endTime = System.currentTimeMillis();
      System.out.println(endTime-startTime);
   }

   public static List<String> readFile(String filePath) {
      try {
         return Files.lines(Paths.get(filePath))
                 .map(line -> line.replace("\"", ""))
                 .filter(line ->  line.matches("^(\\d*\\.{1}\\d)?;?(;\\d*\\.{1}\\d)*;?$"))
                 .toList();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static void writeFile(Set<Set<String>> dataToWrite, String filePath){
      filePath = filePath.replace(".csv", "-result.csv");

      List<Set<String>> sortedSets = new ArrayList<>(dataToWrite);
      Comparator<Set<String>> comparator = (set1, set2) -> Integer.compare(set2.size(), set1.size());
      sortedSets.sort(comparator);

      try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
         int i = 1;
         bufferedWriter.write("Количество групп с более чем одним элементом " + dataToWrite.size());
         bufferedWriter.newLine();
         for (Set<String> data: sortedSets) {
            bufferedWriter.write("Группа " + i);
            bufferedWriter.newLine();
            i++;
            for (String item : data) {
               bufferedWriter.write(item);
               bufferedWriter.newLine();
            }
         }
         System.out.println("Данные успешно записаны в файл: " + filePath);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static Set<Set<String>> getGroups(List<String> data) {
      Set<Set<String>> mergedGroups = new HashSet<>();
      Map<String, Set<String>> groups = groupByValueAndPosition(data);
      groups = groups.entrySet().stream()
              .filter(entry -> entry.getValue().size() > 1)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      for (Set<String> group: groups.values()){
         Set<String> uniqueKeys = getUniqueKeys(group);
         merge(group, uniqueKeys, groups, mergedGroups);
      }
      return mergedGroups;
   }

   private static Set<String> getUniqueKeys(Set<String> set) {
      Set<String> uniqueKeys = new HashSet<>();
      for(String row: set){
         String[] values = row.split(";");
         for (int i = 0; i < values.length; i++){
            if (!values[i].isEmpty()){
               String key = values[i]+"@"+i;
               uniqueKeys.add(key);
            }
         }
      }
      return uniqueKeys;
   }

   private static void merge(Set<String> group,
                             Set<String> uniqueKeys,
                             Map<String, Set<String>> groups,
                             Set<Set<String>> mergedGroups){
      Set<String> setToMerge = new HashSet<>();
      for (String key: uniqueKeys){
         setToMerge = groups.get(key);
         if (setToMerge != null){
            group.addAll(setToMerge);
         }
      }
      mergedGroups.add(group);
   }

   private static Map<String, Set<String>> groupByValueAndPosition(List<String> data) {
      Map<String, Set<String>> groups = new HashMap<>();
      for (String row : data) {
         String[] values = row.split(";");
         for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
               String key = values[i] + "@" + i;
               groups.computeIfAbsent(key, k -> new HashSet<>()).add(row);
            }
         }
      }
      return groups;
   }
}



