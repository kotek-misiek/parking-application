# parking-application
## Test application for ITSharkz

L'application se compose des deux parties: l'application au sense stricte (`parking`) et le serveur démo (`demo-server`). 

### demo-server

Ce dernier sert à l'émulation des données aux formats différents. Des données que j'utilise ici sont pris de l'exemple attaché à la description du test dans l'E-mail.
Ce serveur possède trois endpoints (dans `DemoController`): `demo/token`, `demo/poitiers` et `demo/monpellier`. Des deux premiers enpoints se representent des deux "villes virtuelles". Des données JSON dans tout les deux sont d'un même source (`poitiers_parkings.json` et `poitiers_parkings_realtime.json`) mais sont représentés aux formats différents comme s'ils arriveraient des deux serveurs indépendants.
Le troisième endpoint sert pour la génération des tokens d'autorisation (seulement le serveur `montpellier` exige une autorisation `Bearer` avec un token du serveur `token`).

Des paramètres du `demo-server` sont définis dans le fichier `application.yaml`: le nom de l'application (`spring.application.name`), le port actif (`server.port`) et la liste des chemins qui n'exigent pas l'autorisation (`filter.path.excluded`).  
Si un endpoint attends toutefois une autorisation (dans notre cas actuel, c'est le serveur `montpellier`) elle s'accomplisse dans le filtre `JwtRequestFilter`. 
Ce filtre vérifie l'actualité en la source du token reçu. S'il est incorrect, le filtre retourne un code d'erreur 401 (`Unauthorized`), si tout est en order il envoie la requête à un endpoint.

Des endponts `poitiers` et `monpellier` utilisent des différentes méthodes d'envoyer des paramètres: `poitiers` - le `GET` avec des paramètres dans son URL comme paramètres eux-mêmes, `montpellier` - le `POST` avec des paramètres dans le chemin de l'URL. 
Cette différence a été créée pour montrer des possibilités de la configuration de `parking` dans le traitement des sources différentes.    

### parking

Le serveur `parking` est l'"*application au sense stricte*" - il reçoit des requêtes externes (par exemple du `POSTMAN`), renvoit-les au serveur virtuel approprié (`poitiers` ou `monpellier`), reçoit leurs réponses, analyse-les et trasforme dans un format unifié (`List<ParkingReturnProperties>`) et retourne ces réponses à l'expéditeur.
Des serveurs virtuels retournent des données aux formats différents, c'est pourquoi on doit configurer chaque serveur (plus exactement - un propocole d'echange avec lui) dans le ficher `application.yaml` appartenant au `parking`).

#### Configuration de la communication de l'application avec des serveurs externes

Des paramètres pour confugurer ici sont:
* `spring.application.name` - nom de l'application (pas important, utilisé seulement dans des logs);
* `spring.application.filter.path.included` - le chemin a l'endpoint de l'application (`/api/parking`);
* `parking` et tout ses sub-paramètres - c'est vraiment important.
        
  * `parking.basicParams`, y compris:        
    * `parking.basicParams.proportion`
    * `parking.basicParams.defaultLength`
    
    Ce sont des paramètres par défaut qui définient des frontières géométriques de la fênetre ouvert sur l'écran de téléphone ou de l'ordinateur, montrant le plan de la ville avec des points des marques des parkings (_explication voir_ **Im.1**).
  * `parking.cities` - paramètres des requêtes et des réponses pour chaque des serveurs virtuels des données;
    * `url` - URL de la requête (comme `http://localhost:8090/demo/montpellier/`);
    * `method` - la méthode de la requête (`GET`, `POST`, `PUT`, `DELETE`, `HEAD`);
    * `authorizationType` - le type d'autorisation (ici sont acceptables deux options: `BEARER` et `NO_AUTH` = "_pas d'autorisation_");
    * `paramsType` - des paramètres sont transmis comme `PARAMS` (comme des paramètres normaux) ou comme `PATH` (comme des parties d'un chemin) dans l'URL;
    * `params` - liste des paramètres utilisés dans la requête avec leurs noms selon les exigences du serveur;
      * `latitude` - latitude du point central de la fenêtre (en degrés décimaux);
      * `longitude` - longitude du point central de la fenêtre (en degrés décimaux);
      * `length` - longueur de la fenêtre (en degrés décimaux);
      * `proportion` - proportion entre la longueur et la hauteur de la fenêtre (`longueur/hauteur`); 
      * `latitudeMin` - latitude minmale de la fenêtre (en degrés décimaux);
      * `latitudeMax` - latitude maxmale de la fenêtre (en degrés décimaux);
      * `longitudeMin` - longitude minmale de la fenêtre (en degrés décimaux);
      * `longitudeMax` - longitude maxmale de la fenêtre (en degrés décimaux);
      
      <img src="Picture 1.png" alt="Scheme géométrique des paramètres"/>
      
      **Im.1.** _Scheme géométrique des paramètres_ (toutes les valeurs des paramètres sauf `proportion` sont en degrés décimaux)
       
      * `jsonItem` - expression `JsonPath` exprimant où (dans la réponse JSON) se trouve un bloс de données sur un parking dans la liste;
      * `items` - conformité entre des paramètres à la sortie (toujours des mêmes) et des champs JSON retournés par le service.
        Chaque de ces paramètres a des champs `name` (spécifie le nom du champ correspondant dans la réponse JSON) et `type` (spécifie le type du champ correspondant dans la réponse JSON). Ce dernier peut être nésessaire, parce que le service peut retourner des valeurs `capacity` (capacité d'un parking) and `free` (numéro des places libres à un parking) `Integer` ou `Double`et, par conséquent, ils devront être convertis à `Integer`.

#### Fonctionnement de l'application
La requête (`GET`) doit contenir des paramètres suivants:
* **obligatoires**
  * `city` - nom de la ville du fichier `application.yaml`. Si ça sera un autre nom, pas de cette liste, la requête sera rejetée avec un code d'erreur 400 (`Bad request`).
  * `latitude` - latitude en degrés décimaux avec le point (`.`) décimal, _voir_ **Im.1** 
  * `longitude` - longitude en degrés décimaux avec le point (`.`) décimal, _voir_ **Im.1** 
* **supplémentaires**
  * `length` - longueur de la fenêtre (en degrés décimaux), _voir_ **Im.1**
    
     Valeur par défaut: `parking.basicParams.defaultLength`
  * `proportion` - proportion entre la longueur et la hauteur de la fenêtre (`longueur/hauteur`), _voir_ **Im.1**

    Valeur par défaut: `parking.basicParams.proportion`
* **autorisation**
  * `Authorization` - la requête peut contenir un en-tête `Authorization`. Ça peut être nécessaire seulement, quand un serveur externe exige telle autorisation. 
  
Le résultat de la requête est toujours retourné comme un objet JSON ci-dessous:
`[
  {
    "name": "PALAIS DE JUSTICE",
    "info": "Parking en enclos sous barriéres payant de 9h à 19h du lundi au samedi, gratuit dimanche et jours fériés.",
    "lattutude": 46.58595805,
    "longitude": 0.35129543,
    "capacity": 70,
    "freePlaces": 50
  },
  {
    "name": "MERIMEE",
    "info": "Stationnement résident (Zone verte du lundi au vendredi) - Pour les horaires 2H maximum application du FPS 30€ pour 2H15",
    "lattutude": 46.58922605,
    "longitude": 0.34220112,
    "capacity": -1,
    "freePlaces": -1
  }
]`

Ces réponses contenant toujours des mêmes champs: `name`, `info`, `lattutude`, `longitude`, `capacity` et `freePlaces`, définis dans la classe `ParkingReturnProperties`. Si des champs `capacity` et `freePlaces` ne contiennent que des `-1`, ça signifie que le serveur n'a pas des données sur la charge d'un parking (dans le cas de test - le fichier `poitiers_parkings_realtime.json` n'a pas des parkings avec le nom de `poitiers_parkings.json` ).    

Les services `demo-server` et `parkings` peuvent être testés confortablement avec le `POSTMAN` et sa collection `parking.postman_collection.json` attaché au projet.   

### Justification des choix

La tache principale de cette application était d'avoir la possibilité de ramasser des données des serveurs différents sur des parkings pour les montrer a l'écran de l'odinateur ou du téléphone mobile.
 
Chaque parking a des paramètres principaux: coordonnées géographiques, numéro des places pour des autos et le numéro des places libres. Il y a aussi des informations supplémentaires comme nom propre et certaine description.

Voilà pourquoi j'ai choisi exactement tels champs de la classe `ParkingReturnProperties` et pas des autres.

La liste des parkings est définie par des coordonnées d'un rectangle sur la carte. Ce rectangle a le centre avec sa longitude et latitude tant que sa largeur et longueur. La proportion entre la largeur et la longueur est définie par l'écran d'un téléphone/ordinateur, voilà pourquoi je prenais ces paramètres pour des paramètres de la requête.

Encore un paramètre de la requête est le nom de la ville parce que des différentes villes ont des différents services pour des parkings.

Ma logique sur le choix des options des serveurs est montrée dans le chapitre sur la `Configuration de la communication etc`. 

Mon usage de `JsonPath` est justifié par la simplicité d'analyse des objets `JSON` avec lui.

Généralement, j'ai utilisé le `Spring Boot` et des autres librairies `Java` (version `Java 17`) parce qu'ils sont très confortables et agréables pour créer des services Web. 

#### Possibles lacunes
* Je n'ai créé qu'un test Groovy parce que je n'avais assez de temps pour en couvrir la plupart de mon code.
* Je n'ai pas créé la possibilité d'utilisation des requêtes XML ou l'usage des autres paramètres (sur lesquels je n'ai aucune idée en ce moment-là).
* Je n'ai pas créé la possibilité de l'usage des autres types de l'autorisation sauf le `Bearer`.  

### Conclusion
**L'application que j'ai créé permet de configurer la communication avec chaque service utilisant des requêtes HTTP sans `body` avec l'autorisation `Bearer` ou sans elle et retournant des objets `JSON` avec une liste des parkings avec leurs données.**

