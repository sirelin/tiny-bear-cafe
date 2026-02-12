# Tiny Bear Cafe


### Mängu kirjeldus
- Idee tulenes meie lemmikmängudest, nagu näiteks Stardew Valley, Penguin's Diner, Good Pizza Great Pizza. Me soovisime luua hubase ja armsa ajaviitemängu, mis pakub ka natuke intensiivsust tellimuste täitmise näol. 
- Teemaks on kohvik, kus peategelane on armas karu, kelle vanaema kohvikut ähvardab sulgumine. Mängija peab töötama terve päeva, et saada kokku raha kohviku päästmiseks. Kui mängijal veab ja õnnestub raha kokku saada, siis on õnnelik lõpp. Kui aga mitte, siis on Game Over! 
- Mäng on 2D pixel art stiilis. Kasutatud on pixel art asset packe, mis on saadud itch.io lehelt ning kooskõlas andmekaitseseadustega. Mängu AI komponent esineb kohviku klientide näol. AI klient sisenedes valib endale sobiva laua. Tellimuste täitmine toimub köögis, avaneb popup, kus on väike minigame vastavalt tellimusele. Võimalik on ka boonustena osta endale päeva lõpus upgrade, milleks on erinevad karu skinid. 
- Kui aga on soov sõpradega mängida, siis saavad mänguga liituda sõbrad. Mängijate piiri pole, niikaua, kui TalTechi server kannatab (või localhost) :p


### Mängimise juhend

- How to play?
- Move around with arrow keys: **↑ -> ↓ <-**
- Find a client in the cafe and take their order! When near one, press **SPACE** to take order, and **A** to accept!
- Next, find the correct appliance in the kitchen! Every order has its right appliance, where you can cook or make it. Here’s a hint:
    - Make a coffee near the coffee machines!
    - Put a sandwich together near the cutting board!
    - Take a smoothie from the fridges!
    - Bake a cake in the oven!
    - Mix a salad on the tables!
    - Cook an egg on the stove!
- To help you find the correct appliance, you can press **S** in the kitchen and an arrow will appear on the right machine.
- Once you’re next to the object, press **M** to start making it! A popup will appear that will tell you what letters to press!
- Now that you’ve got your order, find the client and deliver it to them! You can press **T** near them to complete the order. Yay!
- The limit to current orders is **10**. Make sure you complete the previous ones first!
- Press **Q** to close the tutorial. Press **ESC** to quit the game!



### Mängu ja serveri käivitamine lokaalselt
- Copy HTTPS kaudu repositoorium IntelliJ IDEA programmi (Get from VCS)
- Ava nii server kui ka client kaust uues aknas (File -> Open -> client _või_ server -> OK -> New Window)
- Esimesena jooksuta server projekt (server/src/main/java/ee/taltech/cafegame/serverMain.java -> Gradle -> Tasks -> Build -> Build
- Siis ava server Gradle -> Tasks -> Application -> run
- Teisena kliendi projekt (client/core/src/main/java/ee/taltech/cafegame/clientMain.java -> Gradle -> Tasks -> Build -> Build
- Ava klient Gradle -> Tasks -> Application -> run
- Mitme kliendi lubamiseks tuleb IntelliJ’s lubada mitme eksemplari jooksutamine (Run -> Debug Configurations -> Edit Configurations -> Modify options -> Allow multiple instances)
- Seejärel saad uuest jooksutada kliendi faili ning avaneb teine klient.

### Mängu kliendi käivitamine ja TalTech serveriga liitumine
- Copy HTTPS kaudu repositoorium IntelliJ IDEA programmi (Get from VCS)
- Ava client kaust uues aknas (File -> Open -> client -> OK -> New Window)
- Jooksuta kliendi projekt (client/core/src/main/java/ee/taltech/cafegame/clientMain.java -> Gradle -> Tasks -> Build -> Build
- Muuda klassis clientMain.java (asukoht `client/core/src/main/java/ee/taltech/cafegame/clientMain.java`) server aadressi rida "localhost"-ist serveri IP-aadressiks "193.40.255.19", rida 27: 
`private static final String SERVER_ADDRESS = "localhost";` 
-> 
`private static final String SERVER_ADDRESS = "193.40.255.19";`
- Ava klient Gradle -> Tasks -> Application -> run
- Mitme kliendi lubamiseks tuleb IntelliJ’s lubada mitme eksemplari jooksutamine (Run -> Debug Configurations -> Edit Configurations -> Modify options -> Allow multiple instances)
- Seejärel saad uuest jooksutada kliendi faili ning avaneb teine klient.


### Kasutatud tehnoloogiate loetelu
- Java 21.0.6
- LibGDX 1.13.1
- KryoNet 2.24
- Gradle 8.12.1


### Mängu feature'id
- Meie mängus leiduvad funktsionaalsused:
- Tellimused:
    - Selleks et tellimust võtta peab minema kliendi juurde
    - Selleks et tellimus saaks tehtub peab minema kindlasse kohta mapis
    - Igal tellimusel oma koht köögis, kus seda teha saab
    - Kui tellimus on valmistatud ilmub see mängija kätte
    - Selleks et tellimus saaks täidetud ja raha ilmuks mängijale, siis see tuleb viia kliendini
    - Tellimused esinevad minigame’dena ehk popup’id ilmuvad ekraanile
    - Minigame sisu on õigete tähtede vajutamine, et saada tellimus tehtud
    - Olemas on erinevad tellimustega seotud popupid, näiteks tutorial, uue tellimuse vaade, max tellimuste hoiatus
    - Klientide pea kohale ilmuvad tellimuse thought bubble’id
- Lugu:
    - Mängul on konktreetne algus ja lõpp mis on rõhutatud intro ja outroga, mis kannavad ka mängu lugu edasi
    - Mängu saab ‘võita’ tellimuste täitmisega
    - Mängu saab kaasmängijaga koos mängida
    - Mängu saab üksteise vastu mängida, kes kiiremini raha kokku saab
- Ai:
    - Ai kasutab A* pathfindingut, et leida tee lauani
    - Ai annab erinevaid tellimusi
- Ekraanid:
    - Olemas on menüü ekraan
    - Olemas on intro ekraan
    - Olemas on settings ekraan
    - Olemas on mängu ekraan
    - Olemas on outro ekraan
- Tegelane saab liikuda üles-alla, paremale-vasakule
- Olemas on collisionid ehk kokkupõrked
- Tegelased omavahel ei jookse üksteisest läbi, ega seintest ja asjadest
- Kaamera liigub mängijaga kaasa
- Mängul on isetehtud taustamuusika, mis toob mängu kokku terviklikuks
- Hud:
    - Mängija näeb enda edasiminekut rahalises mõttes progressbarilt
    - Tellimused kuvatakse ka HUDis vasakus ääres, see on nn. telimuste järjekord
- Kaart:
    - Olemas on mängu idee kohane disainitud map
    - Olemas on mängu esteetika



### Autorite nimed
- Sirelin Petersell, Liis Renser, Kristiina Marie Palu