package com.giggle.team.api;

import com.fasterxml.jackson.annotation.JsonView;
import com.giggle.team.controller.ChatController;
import com.giggle.team.models.Message;
import com.giggle.team.models.Topic;
import com.giggle.team.models.UserEntity;
import com.giggle.team.repositories.TopicRepository;
import com.giggle.team.repositories.UserRepository;
import com.giggle.team.utils.MessageUtils;
import com.giggle.team.utils.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ChatController chatController;

    @Transactional
    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> signup(@RequestBody UserEntity userEntity) {
        if (Objects.isNull(userRepository.findByEmail(userEntity.getEmail()))) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
            Topic main = topicRepository.findByStompDestination("main");
            if(Objects.isNull(main)){
                main = chatController.initMainChat();
            }
            main.addUser(userEntity);
            userEntity.getTopics().add(main.getId());
            userRepository.save(userEntity);
            topicRepository.save(main);
            List<UserEntity> users = userRepository.findAll();
            for (UserEntity user:
                    users) {
                template.convertAndSendToUser(user.getEmail(), "/queue/service",
                        new Message("service", Message.MessageType.SYSTEM,
                                "USERS_UPDATE", "system", "system", "system"));
            }
            return new ResponseEntity<>("User created", HttpStatus.OK);
        }
        return new ResponseEntity<>("Email exists", HttpStatus.CONFLICT);
    }

    @JsonView(View.Rest.class)
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<UserEntity> entity() {
        return ResponseEntity.ok(new UserEntity(phrase, phrase, phrase));
    }

    @RequestMapping(value = "/chats", method = RequestMethod.GET, produces = "application/json")
    public Page<Topic> chats(Pageable pageable) {
        return topicRepository.findAll(pageable);
    }

}
