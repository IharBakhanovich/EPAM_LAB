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

    /**
     * Provide AuthenticationProvider with the UserDetails, which it catches from DB.
     * AuthenticationProvider uses the PasswordEncoder to validate the password on UserDetails and
     * returns to AuthenticationManager (ProviderManager implementation) an UserDetails object.
     * ProviderManager returns (ultimately, the returned UsernamePasswordAuthenticationToken
     * will be set on the SecurityContextHolder by the authentication Filter) the Authentication
     * that is of type UsernamePasswordAuthenticationToken and has a principal that is the UserDetails.
     *
     * @param nickname is the nickname of the {@link User} that is to find.
     * @return UserDetails that is put to the SecurityContextHolder.
     * @throws UsernameNotFoundException if there is no {@link User} with such a nickname in DB.
     */
    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        Optional<User> user = userDao.findByName(nickname);
        HashSet<Role> roleHashSet = new HashSet<>();
//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user.isPresent()) {
            roleHashSet.add(user.get().getRole());
            return new UserDetailsDto(user.get().getId(), user.get().getNickName(), user.get().getPassword(), roleHashSet);
//            authorities.add(new SimpleGrantedAuthority(user.get().getRole().getName()));
//            return new org.springframework.security.core.userdetails.User(user.get().getNickName(), user.get().getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException(translator.toLocale("USER_NOT_FOUND_WITH_USERNAME") + nickname);
        }
    }
}
