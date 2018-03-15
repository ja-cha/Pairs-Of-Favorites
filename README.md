TO EXECUTE THE CODE, DO ONE OF THE FOLLOWING:

A. Pull this project into IntelliJ
   1.  select 'Create New Project'
   2.  select 'Scala/Sbt'
   3.  Enter a name under 'Project Name' and select this project as 'Project Location'
   4.  click 'Finish'
   5 . Create a new 'Run/Debug Configuration' by  selecting 'Edit Configuration' from 'Run/Debug Configuration' 
       and then click the plus icon in order to add a new 'Application'.
       Fill in  the fields 'Name', 'Working Directory' 'Use classpath of module' and under 'Program Arguments' add 3 items:
       path  | number of minimum desired occurrences | display performance report | display index
       src/main/resources/ArtistList.csv 50 false false
 
B. Run Existing classes
   cd target/scala-2.11/classes/
   scala  FavouritePairs ArtistList.csv 50 false false

C. Compile and run
   scalac src/main/scala/FavouritePairs.scala
   scala FavouritePairs src/main/resources/ArtistList.csv 50 false false

  
BRIEF DESCRIPTION OF CODE (IN TERMS OF RUN TIME COMPLEXITY AND OTHER)

def readData(path:String):Vector[Vector[String]]
 linear time O(N), ( time increases linearly with the size of the input)
 collection executes operations on its elements in parallel (works here because they are not dependent on each other)


def asPairs(data:Vector[Vector[String]]): Vector[(String, String)]= {
   linear time O(N), ( time increases linearly with the size of the input)
   due to 'zipWithIndex' each data row is run twice 
   the index is needed in order to determine starting point, from which to iterate over the list (to determine the pairs)
   the amount of iterations which the collection of pairs takes can be expressed as n(n-1)/2 
   
def collect( pairs:Vector[(String, String)], occurrences:Int): Map[(String, String),Int] 
   This method does the main work of folding (iterating) over the given list of pairs and sticking each into a map, 
   with the pair acting as key and an integer, initialized to 1, as value.
   Each step on the way the map is checked for the existence of the key, if it exists, a new key/value is created, with its value raised by one, 
   and then replaces the old one. In Scala, when using val, not var, for data integrity,  only a copy of the data, rather than an altered version is passed around.
   the resulting list is than filtered (and iterated over again) to only return those pairs which occur at ich occur at least 50 times