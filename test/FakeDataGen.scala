import scala.util.Random

trait FakeDataGen {

  val names = Array("Monnie", "Kenia", "Ira", "Ardis", "Ollie", "Glenna", "Annelle", "Stella", "Jacques", "Jane", "Monet", "Rosanna", "Simona", "Cristina", "Sol",
      "Kelsie", "Dewayne", "Kyra", "Armandina", "Deeanna", "Marx", "Pia", "Monty", "Cristin", "Augusta", "Corina", "Brendan", "Ayana", "Lyndon", "Latoyia", "Angelia",
      "Mitchell", "Leslie", "Alisa", "Tresa", "Casie", "Dustin", "Annabelle", "Renay", "Celinda", "Bella", "Vonda", "Verona", "Freddie", "Nadene", "Everette", "James",
      "Enoch", "Lucas", "Alvera")

  val lastnames = Array("Weber", "Plowman", "Dong", "Glasser", "Sloss", "Alfaro", "Marcantonio", "Whitner", "Botta", "Parchman", "Amerine", "Shirk", "Scroggs", "Lanza",
      "Fail", "Stroop", "Rall", "Yeager", "Pontius", "Numbers", "Brackin", "Lukach", "Zylstra", "Fessenden", "Plude", "Olds", "Timothy", "Swinehart", "Tousignant",
      "Bender", "Tait", "Genest", "Schug", "Quinones", "Timko", "Wireman", "Baumert", "Shunk", "Zellner", "Stefanski", "Wyble", "Braun", "Cadiz", "Catalfamo", "Burnham",
      "Pippins", "Bultman", "Mcquiston", "Monson", "Zerangue")

  def generateName = s"${names(Random.nextInt(names.length))} ${lastnames(Random.nextInt(lastnames.length))}"
}
