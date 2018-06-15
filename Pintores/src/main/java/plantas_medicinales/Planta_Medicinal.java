package plantas_medicinales;

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
import org.apache.jena.vocabulary.SKOS;

/**
 *
 * @author william
 */
public class Planta_Medicinal {
           
    public static void main(String[] args)throws IOException {
        
        Model mimodelo = ModelFactory.createDefaultModel();
        
        //fijamos la ruta donde se crea el rdf
        File f= new File ("/home/calosh/NetBeansProjects/JavaApplication1/Pintores/src/main/java/plantas_medicinales/plantas.rdf"); //Fijar ruta donde se creará el archivo RDF
        FileOutputStream os = new FileOutputStream(f);
        
        //fijamos la URI para las obras
        String prefix_planta = "http://plantas_medicinales.org/planta/";
        mimodelo.setNsPrefix("planta",prefix_planta);
        
        String prefix_enfermedad = "http://enfermedades/enfermedad/";
        mimodelo.setNsPrefix("enfermedad",prefix_enfermedad);
        
        

        String myonto = "http://plantas_ecuador.org/ontology/";
        mimodelo.setNsPrefix("myonto",myonto);
        Model myontoModel = ModelFactory.createDefaultModel();
       // myontoModel.read(myonto);
       

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
            br = new BufferedReader(new FileReader("/home/calosh/NetBeansProjects/JavaApplication1/Pintores/src/main/java/plantas_medicinales/plantas.csv"));
            String line1;
            br.readLine();
            int cont = 0;
            
            Resource plantas_emdicinales = mimodelo.createResource(prefix_planta + "Plantas_medicinales");
            // agergar proiedades
            plantas_emdicinales = plantas_emdicinales.addProperty(RDF.type, SKOS.Concept);
            plantas_emdicinales = plantas_emdicinales.addProperty(SKOS.prefLabel, "Plantas medicinales");
            
            
            Resource enfermedades = mimodelo.createResource(prefix_enfermedad + "Enfermedades");
            // agergar proiedades
            enfermedades = enfermedades.addProperty(RDF.type, SKOS.Concept);
            enfermedades = enfermedades.addProperty(SKOS.prefLabel, "Enfermedades");
            
            while ((line1 = br.readLine()) != null) {
                String[] data = line1.split(",");
                String line = line1;
            
                // Correcion de problema de separacion por comas
                // https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
            String otherThanQuote = " [^\"] ";
            String quotedString = String.format(" \" %s* \" ", otherThanQuote);
            String regex = String.format("(?x) "+ // enable comments, ignore white spaces
                    ",                         "+ // match a comma
                    "(?=                       "+ // start positive look ahead
                    "  (?:                     "+ //   start non-capturing group 1
                    "    %s*                   "+ //     match 'otherThanQuote' zero or more times
                    "    %s                    "+ //     match 'quotedString'
                    "  )*                      "+ //   end group 1 and repeat it zero or more times
                    "  %s*                     "+ //   match 'otherThanQuote'
                    "  $                       "+ // match the end of the string
                    ")                         ", // stop positive look ahead
                    otherThanQuote, quotedString, otherThanQuote);

            String[] tokens = line.split(regex, -1);

                
                
                
                
                
                //System.out.println("Este es el tamañ"+data.length);
                cont = cont+1;
                System.out.println("cuenta"+cont);
                
                String planta_name = tokens[0];
                String planta_uri= tokens[0];
                String definition = tokens[1];
                if (!planta_name.equals("NULL")) {
                    
                    //planta_uri = planta_uri.replaceAll("\\s+", "").replaceAll("\"", "");
                    planta_uri = planta_uri.replace(" ","_");
                    planta_uri = planta_uri.replace(",","");
                    planta_uri = planta_uri.replaceAll("\\s+", "").replaceAll("\"", "");
                    planta_name = planta_name.replace("\"","");
                    definition = definition.replace("\"","");
                    //crear pintores
                    //Resource planta = mimodelo.createResource(prefix_enfermedad + planta_name)
                    //        .addProperty(RDF.type, dboModel.getResource(dbo + "Planta"));
                    
                    //   res1.addProperty(RDF.type, SKOS.Concept);
                    Resource planta = mimodelo.createResource(prefix_planta + planta_uri);
                    // agergar proiedades
                    planta = planta.addProperty(RDF.type, SKOS.Concept);
                    planta = planta.addProperty(SKOS.prefLabel, planta_name);
                    planta = planta.addProperty(SKOS.broader, plantas_emdicinales);
                    planta = planta.addProperty(SKOS.definition, definition);
                    
                    
                    
                  
                    
                    //planta = planta.addProperty(dboModel.getProperty(dbo+ "field"), dboModel.getResource(dbo + "Painting"));
                    //pintor = pintor.addProperty(dboModel.getProperty(dbo+ "field"), dboModel.getResource(dbo + "Drawing"));
                    
                    String enfermedad_name = tokens[2];
                    String enfermedad_uri = tokens[2];
                    //enfermedad_uri = enfermedad_uri.replaceAll("\\s+", "").replaceAll("\"", "");
                    enfermedad_uri = enfermedad_uri.replace(" ","_");
                    enfermedad_uri = enfermedad_uri.replaceAll("\\s+", "").replaceAll("\"", "");

                    Resource enfermedad = mimodelo.createResource(prefix_enfermedad + enfermedad_uri);
                    enfermedad.addProperty(RDF.type, SKOS.Concept);
                    enfermedad = enfermedad.addProperty(SKOS.prefLabel, enfermedad_name);
                    
                    enfermedad = enfermedad.addProperty(SKOS.broader, enfermedades);
                    
                    
                    planta = planta.addProperty(SKOS.related, enfermedad);
                    
                                        
                    /*
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
                        
                        obra = mimodelo.createResource(prefix_planta + uriname1)
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
                    */
                    
                    
                    /*
                    // Relacionar clases
                    if (planta != null) {
                        if (obra != null) mimodelo.add(planta, myontoModel.getProperty(dbo + "author"), obra);
                        
                        //if (tecnica != null) mimodelo.add(obra, myontoModel.getProperty(dbo + "technique"), tecnica);
                        
                        
                    }
*/

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



