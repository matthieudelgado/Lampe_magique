################################
#   READ ME File               #
# Auteurs : - Marc GREGOIRE    #
#           - Matthieu DELAGADO#
#                              #
# Projet ALADYN 2013           #
################################

################################
#-----Utilisation du projet----#
################################

############################
###### Pour Lancer un Main #
############################

Notre main est sous forme d'un Junit pour tester différents appelClient vers un serveur

1)Lancer le serveur : src.server.Server.java
2)Lancer le client  : src.tests.Test.java

Le client est composé de 14 tests

#############################
###### Pour Lancer les tests#
#############################

# Ce package contient 40 Tests Unitaires.
# Seules les methodes publiques ont ete testees

1) Clique droit sur le package test situé au meme niveau que le package src

############################	            
##### BUGS                 #
############################

-Attention ! Le test testCreateAppelClient du Junit : test.tools.TestObjectToXMl.java est à relancer
	            pour qu'il marche. Idem pour le test testToXML  du Junit: test.objets.TestPoint
	        
- Nous avons du modifier la grammaire du dateTime pour qu'elle puisse valider une dateTime.iso8601 
	            
