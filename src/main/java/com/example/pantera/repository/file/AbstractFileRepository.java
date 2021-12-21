package com.example.pantera.repository.file;

import com.example.pantera.domain.Entity;
import com.example.pantera.domain.validators.Validator;
import com.example.pantera.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;

///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    public void loadData(){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line = br.readLine()) != null){
                //if(!line.equals("")) { //de scos afara
                List<String> attributes = Arrays.asList(line.split(";"));
                E entity = extractEntity(attributes);
                super.save(entity);
                System.out.println(line);
                //}
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    protected abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){

        if(super.save(entity) == null){
            writeToFile(entity);
            return null;
        }
        else
            return entity;
    }

    protected void writeToFile(E entity){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,true)))
        {
            bw.write(createEntityAsString(entity));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,false)))
        {
            super.findAll().forEach(entity -> {
                try {
                    bw.write(createEntityAsString(entity));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

//            for (E entity : super.findAll()) {
//                bw.write(createEntityAsString(entity));
//                bw.newLine();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public E delete(ID id) {
        E deleted_entity =  super.delete(id);
        if(deleted_entity != null) {
            writeToFile();
            return deleted_entity;
        }
        return null;
    }

    @Override
    public E update(E entity) {
        E updated_entity = super.update(entity);
        if(updated_entity == null){
            writeToFile();
            return null;
        }
        return entity;
    }
}