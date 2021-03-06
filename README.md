# Pirate sheep

*French here, English further below*

## Disclaimer : 

Je ne suis pas responsable de l'utilisation qui est faite de cette application et elle est fournie sans aucune garantie d'aucune sorte.
I'm by no mean responsible for the way this app is used and this app is available without warranties of any kind.

## Qu'est-ce donc que ceci ?

Cette application vise à générer un pdf reproduisant l'attestation de déplacement dérogatoire nécessaire pour circuler en France durant le confinement dû à la pandémie du Covid19. 

À compter du 6 avril 2020 il est permis d'utiliser une attestation dématérialisée munie d'un QRcode générée par un [site mis en ligne par le governement](https://media.interieur.gouv.fr/deplacement-covid-19/). C'est cette attestation qui est reproduite par cette application.

L'app consiste en un formulaire et une liste des attestations précedemment générées que l'app permet aussi d'afficher.

![Screenshots](./examples/Screenshots.png)

[Voilà un exemple de pdf généré par l'application](./examples/attestation_12_04_2020_17h20.pdf)  

## Pourquoi ? 

Déjà parce que je suis confiné, je m'ennuie et coder cette application est une occupation qui en vaut une autre.

Ensuite parce que ça peut être utile pour plusieurs raisons : 

- Contrairement à un formulaire en ligne, l'application ne nécessite pas d'accès à internet. Tout est généré localement sur le téléphone de l'utilisateur. 
- Les informations de base sont enregistrées automatiquement et préremplies à chaque nouveau lancement de l'app. La date et l'heure de sortie sont aussi renseignées automatiquement mais peuvent être modifiées manuellement. Cela rend la génération de l'attestation très rapide et fait gagner du temps (les confinés n'en manquent pas, les travailleurs plus).
- Actuellement l'app est traduite en français et anglais mais si des personnes sont prêtes à [proposer d'autres traductions ici](https://ethercalc.org/klviivtn1z4h), je peux les intégrer rapidement à l'application et simplifier la vie de ceux qui ne comprennent que peu ou pas le français, car je doute que le site officiel supporte d'autres langues que le français.

Enfin, parce que mon application est libre, le code est disponible, et chacun peut donc vérifier ce que je fais des données qui sont entrées par l'utilisateur (spoiler alert : absolument rien d'autre que générer le pdf et les sauvegarder localement d'un lancement à l'autre), ce qui a le potentiel d'en rassurer certains.

## Questions légales

A priori rien d'illégal à reproduire cette attestation. Il est permis de le faire au stylo sur papier libre, pourquoi pas à l'aide d'une application alors ? L'interdiction précédemment en place d'utiliser des attestations dématérialisées n'avait déjà pas de fondements juridiques solides, alors un fois les documents générés par le site officiel permis, une reproduction à l'identique créée par une application tierce ne devrait pas poser de souci. 



*English*

## What is this thing?

This app aims to generate a pdf format attestation alike what is required to circulate in France during the Covid19 related lock-down. 

Starting April 6th 2020 it is allowed to use a dematerialized attestation with a QRcode generated by [a website from the government](https://media.interieur.gouv.fr/deplacement-covid-19/). It is this attestation this app reproduces.

The app consists in a form and a list of previously generated attestations which can be displayed in-app.

![Screenshots](./examples/Screenshots.png)

[Here's an example of a pdf generated by this app](./examples/attestation_12_04_2020_17h20.pdf)  

## Why ? 

First, because I'm confined, bored and coding is a hobby like any other.

Second, because it can be useful for a number of reasons: 

- Unlike an online form, this app doesn't need internet access. Everything is generated locally on the user's phone.
- Basic user info are cached automatically and prefilled on every subsequent app run. Time and date are also prefilled but can be modified manually. This makes generating the attestation much faster.
- Right now the app is translated to French and English but if anyone has time to [suggest other translations here](https://ethercalc.org/klviivtn1z4h), I can quickly integrate those to the app and make the lives of those who speak little to no french much easier, because I don't think the official form will offer languages other than French.

Finally, because this app is open source, anyone can check out the code, and anyone can check what I do with any data that might be filled by the user (spoiler alert: absolutely nothing other than generating the attestation and caching it between app runs), which can potentially reassure some people.

## Legal questions

There isn't anything illegal in reproducing this attestation. It is allowed to do it with a pen and paper, why not using an app then? It used to be forbidden to use dematerialized attestations which already wasn't legally justified, so since dematerialized attestations from the official website are now alllowed, I see no reason why an identical reproduction from another source would be an issue. 

## /!\ ATTENTION /!\

**Le gouvernement a mis à jour leur site et le QRcode généré par l'app ne correspond plus à celui généré par le site : le contenu textuel est le même mais l'apparence du QRcode est légèrement différente. DANS LE DOUTE UTILISEZ LE SITE OFFICIEL.**

**The government updated their website and the generated QRcode from the app no longer matches that of the official website: the text content is the same but the apparence is slightly different. IN DOUBT JUST USE THE OFFICIAL VERSION.**

## Installation

[Version 1.0.6](https://github.com/guisalmon/pirate_sheep/blob/1.0.6/app/release/app-release.apk)

>Ajout de langues / Languages added : العربية, 中文, Português, ქართული ენა, Castillano, Magyar nyelv, Русский, Polski, Tiếng Việt, Türkçe, Româneşte, বাংলা, پښتو, Italiano, Deutsch
>

[Version 1.0.5](https://github.com/guisalmon/pirate_sheep/blob/1.0.5/app/release/app-release.apk)

>L'app remplit le formulaire avec les infos fournies et le sauvegarde sur le téléphone au format pdf. Le QRcode ne correspond pas tout à fait à la version officielle. L'app permet de lister et d'afficher les attestations précédemment générées.
>
>Fills the official form and saves it on the device as pdf. The QRcode doesn't perfectly match the official version. The app lists and displays previously generated attestations.

## License

GPL-3.0+