package com.giggle.team.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.giggle.team.models.Topic;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.TopicRepository;
import com.giggle.team.repositories.UserRepository;
import com.giggle.team.utils.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Objects;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

  public static final String phrase = "example";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TopicRepository topicRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Transactional
  @JsonView(View.Rest.class)
  @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<String> signup(@RequestBody UserEntity userEntity) {
    if (Objects.isNull(userRepository.findByEmail(userEntity.getEmail()))) {
      userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
      Topic main = topicRepository.findByStompDestination("main");
      main.addUser(userEntity);
      //userEntity.setTopics(Collections.singletonList(main)); // fixme temp crutch
      userRepository.save(userEntity);
      topicRepository.save(main);
      return new ResponseEntity<>("User created", HttpStatus.OK);
    }
    return new ResponseEntity<>("Email exists", HttpStatus.CONFLICT);
  }

  @JsonView(View.Rest.class)
  @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<UserEntity> entity() {
    return ResponseEntity.ok(new UserEntity(phrase, phrase, phrase));
  }

    /*@JsonView(View.Messages.class)
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public Page<UserEntity> persons(String query, Pageable pageable) {
        return userRepository.findByUsernameStartsWith(query, pageable);
    }*/

  @RequestMapping(value = "/chats", method = RequestMethod.GET, produces = "application/json")
  public Page<Topic> chats(Pageable pageable) {
    return topicRepository.findAll(pageable);
  }
}
