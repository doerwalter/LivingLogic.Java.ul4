cp $HOME/.m2/repository/org/python/jython-standalone/2.5.2/jython-standalone-2.5.2.jar ul4jython.jar
#cp jython.jar ul4jython.jar
#zip ul4jython.jar src/main/resources/spark.py src/main/resources/ul4c.py

jar uf ul4jython.jar -C src/main/resources spark.py
jar uf ul4jython.jar -C src/main/resources ul4c.py

