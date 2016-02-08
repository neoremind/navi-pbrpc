package com.baidu.beidou.navi.pbrpc.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchResponse;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoResponse;
import com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoText;
import com.baidu.beidou.navi.pbrpc.demo.service.DemoService;

/**
 * ClassName: DemoServiceImpl <br/>
 * Function: demo服务端
 * 
 * @author Zhang Xu
 */
public class DemoServiceImpl implements DemoService {

    /**
     * 默认返回，暂时无用
     */
    @SuppressWarnings("unused")
    private static final User DEFAULT_USER = new User(1, "name-", User.Gender.MALE);

    /**
     * @see com.baidu.beidou.navi.pbrpc.demo.service.DemoService#doSmth(com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoRequest)
     */
    @Override
    public DemoResponse doSmth(DemoRequest req) {
        User user = cachedUsers.get(req.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        DemoResponse.Builder builder = DemoResponse.newBuilder();
        builder.setUserId(user.getUserId());
        builder.setUserName(user.getUserName());
        if (user.getGender().equals(User.Gender.MALE)) {
            builder.setGenderType(DemoResponse.GenderType.MALE);
        } else {
            builder.setGenderType(DemoResponse.GenderType.FEMALE);
        }
        return builder.build();
    }

    @Override
    public DemoResponse doSmthTimeout(DemoRequest req) {
        System.out.println("doSmthTimeout....");
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return doSmth(req);
    }

    /**
     * @see com.baidu.beidou.navi.pbrpc.demo.service.DemoService#doSmthBatch(com.baidu.beidou.navi.pbrpc.demo.proto.Demo.DemoBatchRequest)
     */
    @Override
    public DemoBatchResponse doSmthBatch(DemoBatchRequest req) {
        int requestSize = req.getRequestSize();
        DemoBatchResponse.Builder builder = DemoBatchResponse.newBuilder();
        List<DemoText> list = new ArrayList<DemoText>(requestSize);
        for (int i = 0; i < requestSize; i++) {
            DemoText.Builder demoText = DemoText.newBuilder();
            demoText.setText(req.getText());
            list.add(demoText.build());
        }
        builder.addAllTexts(list);
        return builder.build();
    }

    /**
     * 缓存的user列表
     */
    private Map<Integer, User> cachedUsers = new HashMap<Integer, User>();

    /**
     * 初始化
     */
    {
        for (int i = 0; i < 10; i++) {
            User user = new User(i, "name-" + i, i % 2 == 0 ? User.Gender.MALE : User.Gender.FEMALE);
            cachedUsers.put(user.getUserId(), user);
        }
    }

    /**
     * ClassName: User <br/>
     * Function: 用户VO
     * 
     * @author Zhang Xu
     */
    static class User {

        /**
         * userid
         */
        private int userId;

        /**
         * username
         */
        private String userName;

        /**
         * 性别
         */
        private Gender gender;

        /**
         * Creates a new instance of User.
         * 
         * @param userId
         * @param userName
         * @param gender
         */
        public User(int userId, String userName, Gender gender) {
            super();
            this.userId = userId;
            this.userName = userName;
            this.gender = gender;
        }

        /**
         * 性别枚举
         */
        public static enum Gender {
            MALE, FEMALE;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

    }

}
