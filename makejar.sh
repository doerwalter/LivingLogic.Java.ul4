cp jython.jar ul4jython.jar
#zip ul4jython.jar src/main/resources/spark.py src/main/resources/ul4c.py

jar uf ul4jython.jar -C src/main/resources spark.py
jar uf ul4jython.jar -C src/main/resources ul4c.py

