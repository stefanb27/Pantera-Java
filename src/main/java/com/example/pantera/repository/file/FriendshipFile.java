package com.example.pantera.repository.file;

import com.example.pantera.domain.Friendship;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.validators.Validator;

import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {
    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    protected Friendship extractEntity(List<String> attributes) {
        long id1 = Long.parseLong(attributes.get(0));
        long id2 = Long.parseLong(attributes.get(1));
        Friendship friendship = new Friendship();
        Tuple t = new Tuple(id1, id2);
        friendship.setId(t);
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight();
    }

    public void synchronize(){
        writeToFile();
    }
}

















































//package socialNetwork.repository.file;
//
//import socialNetwork.domain.Entity;
//import socialNetwork.domain.User;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.List;
//
//public class FriendshipFile {
//    UserFile userFile;
//    String fileNameFriends;
//
//    public FriendshipFile(String fileNameFriends, UserFile userFile) {
//        this.fileNameFriends = fileNameFriends;
//        this.userFile = userFile;
//        loadDataFriends();
//    }
//
//    public void loadDataFriends() {
//        try (BufferedReader br = new BufferedReader(new FileReader(fileNameFriends))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                //if(!line.equals("")) { //de scos afara
//                List<String> attributes = Arrays.asList(line.split(";"));
//                User user = userFile.findOne(Long.parseLong(attributes.get(0)));
//                for (int i = 1; i < attributes.size(); i++) {
//                    user.addFriend(userFile.findOne(Long.parseLong(attributes.get(i))));
//                }
//                System.out.println(line);
//                //}
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void writeFriends(UserFile userFile) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameFriends, false))) {
//            Iterable<User> it = userFile.findAll(); //.forEach(user -> {
//            for(User i : it){
//                if (i.getFriends().size() != 0) {
//                    StringBuilder string = new StringBuilder(i.getId() + ";");
//                    System.out.println("Smecherul de " + i.getLastName() + " are " + i.getFriends().size() + " prieteni");
//                    for (User user1 : i.getFriends()) {
//                        //System.out.println("Prieniu lui : " + user.getLastName() + " este : " + user1.getLastName());
//                        string.append(user1.getId()).append(";");
//                    }
//                    bw.write(string.toString());
//                    bw.newLine();
//                }
//            }
//
//                //try {
//
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//           // });
//
////            for (E entity : super.findAll()) {
////                bw.write(createEntityAsString(entity));
////                bw.newLine();
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
