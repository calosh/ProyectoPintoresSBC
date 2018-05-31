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
public class Prueba {
           
    public static void main(String[] args)throws IOException {
        
        Model mimodelo = ModelFactory.createDefaultModel();
        
        //fijamos la ruta donde se crea el rdf
        File f= new File ("/home/calosh/NetBeansProjects/JavaApplication1/Pintores/src/main/java/com/mycompany/pintores/artistas.rdf"); //Fijar ruta donde se creará el archivo RDF
        FileOutputStream os = new FileOutputStream(f);
        
        //fijamos la URI 
        String prefix1 = "http://videosmusicales.org/videos/";
        mimodelo.setNsPrefix("data1",prefix1);

        String myonto = "http://videosmusicales.org/ontology/";
        mimodelo.setNsPrefix("myonto",myonto);
        Model myontoModel = ModelFactory.createDefaultModel();
       // myontoModel.read(myonto);

        //Fijar prefijos de vocabularios incorporados en Jena
        String foaf = "http://xmlns.com/foaf/0.1/";
        mimodelo.setNsPrefix("foaf",foaf);

        //Fijar Prefijo para otros vocabularios como DBPedia que no están directamente incorporados en Jena
        String dbo = "http://dbpedia.org/ontology/";
        mimodelo.setNsPrefix("dbo", dbo);
        Model dboModel = ModelFactory.createDefaultModel();
        dboModel.read(dbo);

        String dbr = "http://dbpedia.org/resource/";
        mimodelo.setNsPrefix("dbr", dbr);
        Model dbrModel = ModelFactory.createDefaultModel();
        dbrModel.read(dbr);
        
        //String gndo = "http://d-nb.info/standards/elementset/gnd#";
        //mimodelo.setNsPrefix("gndo", gndo);
        //Model gndoModel = ModelFactory.createDefaultModel();
        //gndoModel.read(gndo);

        String ebu = "http://www.ebu.ch/metadata/ontologies/ebucore/ebucore#";
        mimodelo.setNsPrefix("ebu", ebu);
        Model ebuModel = ModelFactory.createDefaultModel(); // modelo para ebucore
        ebuModel.read(ebu);

        String rdag2 = "http://rdvocab.info/ElementsGr2/";
        mimodelo.setNsPrefix("rdag2", rdag2);
        Model rdag2Model = ModelFactory.createDefaultModel(); // modelo para rdag2
        rdag2Model.read(rdag2);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/home/calosh/NetBeansProjects/JavaApplication1/Pintores/src/main/java/com/mycompany/pintores/artistas.csv"));
            String line;
            br.readLine();
            int cont = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                //System.out.println("Este es el tamañ"+data.length);
                cont = cont+1;
                System.out.println("cuenta"+cont);
                String uriname = data[0];

                if (!uriname.equals("NULL")) {

                    uriname = uriname.replaceAll("\\s+", "").replaceAll("\"", "");
                    //System.out.println(uriname);
                    //crear artistas
                    Resource artistas = mimodelo.createResource(prefix1 + uriname)
                            .addProperty(RDF.type, dboModel.getResource(dbo + "MusicalArtist"));
                    if (!data[3].equals("NULL")) artistas = artistas.addLiteral(FOAF.nick, data[3]);
                    if (!data[10].equals("NULL")) artistas = artistas.addLiteral(FOAF.gender, data[10]);

                            //.addProperty(RDF.type, VCARD.AGENT)
                    artistas = artistas.addLiteral(VCARD.FN, data[0]);

                    //if (!data[1].equals("NULL")) artistas = artistas.addLiteral(gndoModel.getProperty(gndo + "placeOfBirth"), data[1]);
                    if (!data[2].equals("NULL")) artistas = artistas.addLiteral(dboModel.getProperty(dbo+ "hasRole"), data[2]);
                    if (!data[6].equals("NULL")) artistas = artistas.addLiteral(rdag2Model.getProperty(rdag2 + "periodOfActivityOfThePerson"), data[6]);

                    Resource website = null;
                    if (!data[7].equals("NULL")) {
                        website = mimodelo.createResource( prefix1 + uriname + "_" + "website")
                                .addProperty(RDF.type, myontoModel.getResource(dbo + "website"))
                                .addLiteral(RDFS.label, data[7]);
                    }

                    Resource genre = null;
                    if (!data[4].equals("NULL")) {
                        String uriname4 = data[4].replace(" ","_");
                        genre = mimodelo.createResource(prefix1 + uriname4)
                                .addProperty(RDF.type, myontoModel.getResource(dbo + "Genre"))
                                .addLiteral(RDFS.label, data[4]);
                    }

                    if (website != null) mimodelo.add(artistas, dboModel.getProperty(myonto + "haswebsite"), website);
                    if (genre != null) mimodelo.add(artistas, dboModel.getProperty(myonto + "hasmusicGenre"), genre);

                    String uriname2 = data[15];
                    Resource videos = null;
                    if (!uriname2.equals("NULL")) {
                        uriname2 = uriname2.replaceAll("\\s+", "").replaceAll("\"", "");
                        videos = mimodelo.createResource(prefix1 + uriname2);
                        if (!data[16].equals("NULL")) videos = videos.addProperty(ebuModel.getProperty(ebu + "title"), data[16]);
                        if (!data[14].equals("NULL")) videos = videos.addProperty(ebuModel.getProperty(ebu + "date"), data[14]);
                        if (!data[17].equals("NULL")) videos = videos.addProperty(ebuModel.getProperty(ebu + "description"), data[17]);
                        videos = videos.addLiteral(ebuModel.getProperty(ebu + "source"), data[15]);
                        videos = videos.addProperty(RDF.type, ebuModel.getResource(ebu + "YouTubeVideo"));
                    }

                    String uriname3 = data[13];
                    Resource channel = null;
                    if (!uriname3.equals("NULL")) {
                        channel = mimodelo.createResource(prefix1 + uriname3)
                                .addProperty(RDF.type, ebuModel.getResource(ebu + "PublicationChannel"));
                        if (!data[12].equals("NULL")) channel = channel.addLiteral(RDFS.label, data[12])
                                .addLiteral(ebuModel.getProperty(ebu + "publicationChannelName"), data[12]);
                        channel = channel.addLiteral(ebuModel.getProperty(ebu + "source"), data[13]);
                    }

                    String uriname4 = data[11];
                    Resource type = null;
                    if (!uriname4.equals("NULL")) {
                        type = mimodelo.createResource(prefix1 + uriname4)
                                .addProperty(RDF.type, myontoModel.getResource(myonto + "type"))
                                .addProperty(RDFS.label, data[11]);
                    }

                    System.err.printf("Type: %s \nMiModelo: %s \nVideos: %s \nMyOntoModel: %s\n", type, mimodelo, videos, myontoModel);
                    if (videos != null) {
                        if (type != null) mimodelo.add(videos, myontoModel.getProperty(myonto + "belong"), type);
                        if (channel != null)
                            mimodelo.add(videos, ebuModel.getProperty(ebu + "hasPublicationChannel"), channel);

                        if (artistas != null) mimodelo.add(artistas, ebuModel.getProperty(ebu + "hasvideos"), videos);
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



