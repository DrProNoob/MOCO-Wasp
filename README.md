# MOCO WASP Projekt Freundschaft über Distanz
Unsere App soll Nutzern helfen, Freundschaften über Distanz zu pflegen und zu vertiefen. Dazu bietet die App die Möglichkeit, sich mit Freunden über einen Chat auszutauschen. Die Hauptkomponente der App besteht darin, dass sie den Freunden jeweils Challenges stellt, die zu mehr Austausch zwischen ihnen führen sollen. Außerdem möchten wir mit der App Freunde dazu anregen, neue Erfahrungen miteinander zu machen und Erinnerungen zu schaffen. Das Ziel der App ist aber auch, nicht nur Funktionalität zu bieten, sondern auch für uns als Entwickler Erfahrungen im Bereich Kotlin Multiplatform-Entwicklung zu sammeln.

Der Hauptscreen der App zeigt dem Nutzer, was er selbst und sein Freund/ seine Freundin gepostet haben. Die Posts der Nutzer können Bilder sein oder das Ergebnis von erledigten Challenges. Auf dem Hauptscreen hat man auch die Möglichkeit, eine zufällige Challenge zu starten.

Ein weiterer Screen ist der Chat-Screen, über den man mit seinem Freund/ seiner Freundin schreiben kann. In diesen Screen soll man zukünftig mittels einer Navigationsleiste gelangen. Momentan erreicht man ihn durch eine Chat-Challenge.

Als drittes gibt es noch einen Screen für einen Schrittzähler. Dieser zeigt, die vom Sensor des Gerätes gelesenen Schritte an.

Bei den Challenges gibt es die Chat-Challenge, die einen dazu auffordert, mit einem Freund über den Chat zu kommunizieren. Eine weitere Challenge besteht darin, einem Freund ein bestimmtes Bild zu schicken, wie zum Beispiel: "Mache ein Bild von einem Hund." Die dritte Challenge ist besteht darin eine bestimmte anzahl an Schritten innerhalb von einem Tag zu laulfen.


## Video Demo

## Welche Probleme gab es?
Das erste Problem, das wir hatten, war die Frage, wie wir die Ordnerstruktur effizient und sauber organisieren. Wir haben uns für eine feature-basierte Paketstruktur entschieden, die unseren Anforderungen entspricht. Die Features haben wir in separaten Branches entwickelt und anschließend problemlos in den Main-Branch gemerged.

Weitere Schwierigkeiten traten bei der Implementierung der Kamera- und Schrittzählerfunktionalität auf. Besonders bei iOS musste man sich tief in die Dokumentation einarbeiten, da diese meist auf Swift oder Objective-C ausgelegt ist und nicht auf Kotlin. Die Kamera-Funktionalität für iOS war besonders aufwendig. Man musste herausfinden, wie man auf die Kamera zugreift, die verschiedenen Objektive ansteuert, feststellen, dass es keine Kamera im Emulator gibt, und verstehen, wie Dispatcher in iOS für Nebenläufigkeit funktionieren... Fas ist nur ein Bruchteil der Herausforderungen, um die Kamera-Vorschau überhaupt anzeigen zu können. Es gab viel zu lernen, aber jetzt sind wir für die Zukunft gut gerüstet.

Für Android war die Integration der Kamera etwas einfacher, da es dafür ausreichend Dokumentation gibt.


## Architektur
Die App Architektur basiert auf Model-View-ViewModel Architektur. Wir haben Firebase Realtime Database als Datenbank verwendet und Firebase Storage um die kompriemierten Fotos abzuspeichern.

![ArchitekturImage](https://github.com/DrProNoob/MOCO-Wasp/blob/main/gitAsstets/Architektur.jpg)


## Externe Libaries
|      Verwendet     |     Quelle   |
| ------------- | ------------- |
|  Koin DI  |    inserKoinIo/Koib   |
|  Firebase  | Gitlife Firebase |
|  Permission   | calf/Permission |
|  Http Client  | Ktor  |
|  Kamel Async Image Loading   | Kamel-Media/Kamel|

## Was tun um die App zum laufen zu bringen
