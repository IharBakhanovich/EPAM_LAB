package com.epam.esm.service.impl;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dto.UserDetailsDto;
import com.epam.esm.model.impl.Role;
import com.epam.esm.model.impl.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private Translator translator;
    @Autowired
    private ConversionService conversionService;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Optional<User> user = userDao.findByName(nickname);
        HashSet<Role> roleHashSet = new HashSet<>();

        if (user.isPresent()) {
            roleHashSet.add(user.get().getRole());
            return new UserDetailsDto(user.get().getId(), user.get().getNickName(), user.get().getPassword(), roleHashSet);
//            return conversionService.convert(
//                    new User(user.get().getId(), user.get().getNickName(), user.get().getPassword(), user.get().getRole()),
//                    UserDetails.class);
        } else {
            throw new UsernameNotFoundException(translator.toLocale("USER_NOT_FOUND_WITH_USERNAME") + nickname);
        }
        //todo to check whether conversion service can cope with this task
//        return userDao.findByName(nickname)
//                .orElseThrow(() -> new UsernameNotFoundException(translator
//                        .toLocale("USER_NOT_FOUND_WITH_USERNAME") + nickname));
    }
}
