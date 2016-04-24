package controllers

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

  val text = List(
    "They picked up the gear from the boat",
    "The old man carried the mast on his shoulder and the boy carried the wooden boat with the coiled, hard-braided brown lines, the gaff and the harpoon with its shaft",
    "The box with the baits was under the stern of the skiff along with the club that was used to subdue the big fish when they were brought alongside",
    "No one would steal from the old man but it was better to take the sail and the heavy lines home as the dew was bad for them and, though he was quite sure no local people would steal from him, the old man thought that a gaff and a harpoon were needless temptations to leave in a boat",
    "They walked up the road together to the old man’s shack and went in through its open door",
    "The old man leaned the mast with its wrapped sail against the wall and the boy put the box and the other gear beside it",
    "The mast was nearly as long as the one room of the shack",
    "The shack was made of the tough budshields of the royal palm which are called guano and in it there was a bed, a table, one chair, and a place on the dirt floor to cook with charcoal",
    "On the brown walls of the flattened, overlapping leaves of the sturdy fibered guano there was a picture in color of the Sacred Heart of Jesus and another of the Virgin of Cobre",
    "These were relics of his wife",
    "Once there had been a tinted photograph of his wife on the wall but he had taken it down because it made him too lonely to see it and it was on the shelf in the corner under his clean shirt.",
    "When the boy came back the old man was asleep in the chair and the sun was down.",
    "The boy took the old army blanket off the bed and spread it over the back of the chair and over the old man’s shoulders.",
    "They were strange shoulders, still powerful although very old, and the neck was still strong too and the creases did not show so much when the old man was asleep and his head fallen forward."
  )
  
  def generateFullName = s"${names(Random.nextInt(names.length))} ${lastnames(Random.nextInt(lastnames.length))}"

  def generateName = s"${names(Random.nextInt(names.length))}"

  def generateText(maxCharacters: Integer) = {
    val shuffledText = Random.shuffle(text): List[String]
    val stitchedText = shuffledText.reduceLeft((a, b) => a + b)
    stitchedText.substring(0, maxCharacters)
  }

}
