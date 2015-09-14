#wGPS
Cette application permet à un utilisateur de se localiser sur une carte en temps réel. L'application suit en temps réel la position de l'utilisateur en lui fournissant la position la plus précise possible.

En mode portrait, l'application dispose d'un menu "drawer" à tirer depuis la gauche de l'écran permettant de gagner de l'espace. Ce menu indique à l'utilisateur ses coordonnées actuelles ainsi que la précision de sa position en mètres.

En mode paysage, ce menu n'est pas affiché mais un écran présentant une interface similaire est affiché à coté de la carte.

L'utilisateur peut choisir le type de carte à afficher grâce a un bouton situé en bas a droite de la carte. Si l'utilisateur change l'orientation de son téléphone, ce choix est sauvegardé. La position de l'utilisateur reste au centre de l'écran mais l'utilisateur peut  contrôler le zoom qu'il désire utiliser.

Il est également possible d'enregistrer votre position actuelle grâce au bouton "+" également situé en bas de l'écran. Cette position apparait ensuite dans le menu de l'application et peut être affichée sur la carte grâce à un appui sur le nom de la position.

#Choix technologiques
L'affichage de la carte se fait pour l'instant uniquement grâce à l'API google Maps. Les solutions Bing et Open Street Map n'ont pas encore été étudiées.

Pour le GPS, l'application vérifie que l'utilisateur a bien son GPS activé au lancement de l'application. Si jamais le GPS n'est pas réglé sur haute précision, l'utilisateur en sera informé. La position peut être déterminée par GPS ou grâce au réseau. A chaque fois, la position la plus précise entre le GPS et le réseau sera utilisée. Ceci pourrait être amélioré en vérifiant si des positions plus anciennes que celles tout juste relevées sont plus précises ou non. Un LocationListener est utilisé pour déterminer les changements d'états du GPS. Si jamais la position de l'utilisateur ne peut être déterminée ni via le GPS ni via le réseau, alors un bandeau indiquera a l'utilisateur que le signal GPS a été perdu. La position GPS est envoyée régulièrement si jamais l'utilisateur se déplace. Le temps  et la distance minimum entre deux positions est faible (500ms, 1m) mais ceci semble nécessaire pour cette application. En cas de consommation de batterie élevée, ces valeurs devront être modifiées. 

La connectivité réseau est également monitorée. Un receiver permet de recevoir les changements d'états du réseau. L'application ne vérifie pas si une connection existe bel et bien pour ne pas multiplier les appels réseaux. Cependant, si ni le wifi ni le réseau cellulaire n'est disponible, l'utilisateur en sera averti grâce à un bandeau au bas de l'écran. 

#Dépendances
https://github.com/Karumi/ExpandableSelector
https://github.com/markushi/android-circlebutton