package com.mycompany.pintores;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.jena.rdf.model.*;
import java.io.BufferedReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.rdf.model.RDFWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.jena.ontology.OntModelSpec;

/**
 *
 * @author william
 */
public class Pintor {
           
    public static void main(String[] args)throws IOException {
        
        Model mimodelo = ModelFactory.createDefaultModel();
        
        //fijamos la ruta donde se crea el rdf
        File f= new File ("/home/calosh/NetBeansProjects/JavaApplication1/Pintores/src/main/java/com/mycompany/pintores/artistas.rdf"); //Fijar ruta donde se creará el archivo RDF
        FileOutputStream os = new FileOutputStream(f);
        
        //fijamos la URI para las obras
        String prefix_obras = "http://artistas_ecuador.org/obras/";
        mimodelo.setNsPrefix("obra",prefix_obras);
        
        String prefix_artista = "http://artistas_ecuador.org/artista/";
        mimodelo.setNsPrefix("artista",prefix_artista);
        
        

        String myonto = "http://artistas_ecuador.org/ontology/";
        mimodelo.setNsPrefix("myonto",myonto);
        Model myontoModel = ModelFactory.createDefaultModel();
       // myontoModel.read(myonto);
       
       
        String prefix_tipo = "http://artistas_ecuador.org/tipo/";
        mimodelo.setNsPrefix("tipo",prefix_tipo);

        String foaf = "http://xmlns.com/foaf/0.1/";
        mimodelo.setNsPrefix("foaf",foaf);

        String dbo = "http://dbpedia.org/ontology/";
        mimodelo.setNsPrefix("dbo", dbo);
        Model dboModel = ModelFactory.createDefaultModel();
        dboModel.read(dbo);
        
        String dbp = "http://dbpedia.org/property/";
        mimodelo.setNsPrefix("dbp", dbp);
        Model dbpModel = ModelFactory.createDefaultModel();
        dbpModel.read(dbp);

       

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/home/calosh/NetBeansProjects/JavaApplication1/Pintores/src/main/java/com/mycompany/pintores/pinturas_Eduardo.csv"));
            String line;
            br.readLine();
            int cont = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                //System.out.println("Este es el tamañ"+data.length);
                cont = cont+1;
                System.out.println("cuenta"+cont);
                
                String fullName = data[0];
                if (!fullName.equals("NULL")) {

                    fullName = fullName.replaceAll("\\s+", "").replaceAll("\"", "");
                    //crear pintores
                    Resource pintor = mimodelo.createResource(prefix_artista + fullName)
                            .addProperty(RDF.type, dboModel.getResource(dbo + "Painter"));
                    // agergar proiedades
                    pintor = pintor.addProperty(VCARD.FN, data[0]);
                    pintor = pintor.addProperty(dboModel.getProperty(dbo+ "field"), dboModel.getResource(dbo + "Painting"));
                    //pintor = pintor.addProperty(dboModel.getProperty(dbo+ "field"), dboModel.getResource(dbo + "Drawing"));

                    Resource obra = null;
                    if (!data[1].equals("NULL")) {
                        String uriname1 = "";
                        if(data[1].equals("S/T")){
                             uriname1 = "Sin_título_oficial_"+cont;
                        }else{
                             uriname1 = data[1].replace(" ","_");
                             uriname1 = uriname1.replace("S/T","S_T");
                             uriname1 = uriname1.replace("/","_");
                        }
                        
                        obra = mimodelo.createResource(prefix_obras + uriname1)
                                .addProperty(RDF.type, myontoModel.getResource(dbo + "Painting"))
                                .addProperty(RDFS.label, data[1]);
                        
                        obra = obra.addProperty(myontoModel.getProperty(dbo + "year"), data[2]);
                        obra = obra.addProperty(myontoModel.getProperty(dbo + "title"), data[1]);
                        obra = obra.addProperty(myontoModel.getProperty(dbp + "dimensions"), data[4]);
                        obra = obra.addProperty(myontoModel.getProperty(dbo + "picture"), data[6]);
                        obra = obra.addProperty(myontoModel.getProperty(dbo + "wikiPageExternalLink"), data[5]);
                        obra = obra.addProperty(myontoModel.getProperty(dbo + "technique"), data[3]);
                        
                        //dbo:technique
                          

                    }
                    
                    /*
                    Resource tecnica = null;
                    if (!data[3].equals("NULL")) {
                        String uriname1 = data[3].replace(" ","_");
                        tecnica = mimodelo.createResource(prefix_tipo + uriname1)
                                .addProperty(RDF.type, myontoModel.getResource(myontoModel + "Tecnica"))
                                .addProperty(RDFS.label, data[3]);
                        
                        tecnica = tecnica.addProperty(myontoModel.getProperty(dbp + "title"), data[3]);
                        System.out.println("AQUI");
                        System.out.println(data[3]);
                    }
                    */
                    

                    
                    
                    
                    // Relacionar clases
                    if (pintor != null) {
                        if (obra != null) mimodelo.add(pintor, myontoModel.getProperty(dbo + "author"), obra);
                        
                        //if (tecnica != null) mimodelo.add(obra, myontoModel.getProperty(dbo + "technique"), tecnica);
                        
                        
                    }

                }
                               
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // list the statements in the Model
        StmtIterator iter = mimodelo.listStatements();
        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
            
            
        }

        // now write the model in XML form to a file
        System.out.println("MODELO RDF------");
        mimodelo.write(System.out, "RDF/XML-ABBREV");

        // Save to a file
        RDFWriter writer = mimodelo.getWriter("RDF/XML"); //RDF/XML
        writer.write(mimodelo, os, "");

        //Cerrar modelos
        dboModel.close();
        mimodelo.close();

    }
}



