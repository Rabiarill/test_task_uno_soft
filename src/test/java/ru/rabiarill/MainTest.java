package ru.rabiarill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class MainTest {


   @Test
   void getGroups_ValidData(){
      var result = Main.getGroups(getTestData());

      assertEquals(getTestAns(), result);
   }

   private List<String> getTestData(){
      return List.of(
              "111;123;222",
              "200;123;100",
              "300;200;100",
              ";300;432",
              ";888;887"
      );
   }

   private Set<Set<String>> getTestAns(){
      return  Set.of(
               Set.of("111;123;222",
                       "200;123;100",
                       "300;200;100")
               );
   }

}