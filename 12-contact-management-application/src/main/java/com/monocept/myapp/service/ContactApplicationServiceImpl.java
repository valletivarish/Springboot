package com.monocept.myapp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.ContactRequestDto;
import com.monocept.myapp.dto.ContactResponseDto;
import com.monocept.myapp.dto.UserRequestDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.entity.Contact;
import com.monocept.myapp.entity.PagedResponse;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.ContactApiException;
import com.monocept.myapp.exception.NoContactNotFoundException;
import com.monocept.myapp.exception.NoUserRecordFoundException;
import com.monocept.myapp.repository.UserRepository;

@Service
public class ContactApplicationServiceImpl implements ContactApplicationService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public PagedResponse<UserResponseDto> getAllUsers(int page, int size, String sortBy, String direction) {
		Sort sort = Sort.by(sortBy);
		if (Sort.Direction.DESC.equals(direction)) {
			sort.descending();
		} else {
			sort.ascending();
		}
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<User> users = userRepository.findAll(pageRequest);
		return new PagedResponse<UserResponseDto>(convertUserToUserResponseDto(users.getContent()), users.getNumber(), users.getSize(),
				users.getTotalElements(), users.getTotalPages(), users.isLast());
	}

	@Override
	public UserResponseDto createAndUpdateUser(UserRequestDto userRequestDto) {
		if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ContactApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }
		if(findByUserNameAndActive()) {
			User user = convertUserRequestDtoToUser(userRequestDto);
			User userResponse = null;
			if (userRequestDto.getUserId() == 0) {
				return convertUserToUserResponseDto(userRepository.save(user));
			}
			User userFound = userRepository.findById(userRequestDto.getUserId()).orElse(null);
			if (userFound == null) {
				throw new NoUserRecordFoundException("User with id " + user.getUserId() + " not found");
			}
			if(userRequestDto.getFirstName()!=null && userRequestDto.getFirstName()!="") {
				userFound.setFirstName(userRequestDto.getFirstName());
			}
			if(userRequestDto.getLastName()!=null && userRequestDto.getLastName()!="") {
				userFound.setLastName(userRequestDto.getLastName());
			}
			if(userRequestDto.getEmail()!=null && userRequestDto.getEmail()!="") {
				userFound.setEmail(userRequestDto.getEmail());
			}
			if(userRequestDto.isActive()!= userFound.isActive()) {
				userFound.setActive(userRequestDto.isActive());
			}
			userResponse = userRepository.save(userFound);
			return convertUserToUserResponseDto(userResponse);
		}
		throw new NoUserRecordFoundException("admin is currently not active cannot perform operations");
	}

	private boolean findByUserNameAndActive() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
			if(user!=null && user.isActive()) {
				return true;
			}
		}
		return false;
	}

	

	@Override
	public UserResponseDto getUserById(long id) {
		User user=null;
		if(findByUserNameAndActive()) {
			user = userRepository.findById(id).orElse(null);
			if(user==null) {
				throw new NoUserRecordFoundException("User with id " + user.getUserId() + " not found");
			}
			
		}
		return convertUserToUserResponseDto(user);
	}

	@Override
	public String deleteUser(long id) {
		if(findByUserNameAndActive()) {
			User user = userRepository.findById(id).orElse(null);
			if(user==null) {
				throw new NoUserRecordFoundException("User with id " + id + " not found");
			}
			if(user.isActive()==false) {
				throw new NoUserRecordFoundException("User with id " + id + " not active");
			}
			user.setActive(false);
			userRepository.save(user);
		}
		
		return "deleted successcfully";
	}
	
	private UserResponseDto convertUserToUserResponseDto(User user) {
		UserResponseDto userResponseDto = new UserResponseDto();
		userResponseDto.setUserId(user.getUserId());
		userResponseDto.setFirstName(user.getFirstName());
		userResponseDto.setLastName(user.getLastName());
		userResponseDto.setActive(user.isActive());
		userResponseDto.setAdmin(user.isAdmin());
		userResponseDto.setEmail(user.getEmail());
		return userResponseDto;
	}

	private User convertUserRequestDtoToUser(UserRequestDto userRequestDto) {
		User user = new User();
		user.setAdmin(userRequestDto.isAdmin());
		user.setActive(userRequestDto.isActive());
		user.setEmail(userRequestDto.getEmail());
		user.setFirstName(userRequestDto.getFirstName());
		user.setLastName(userRequestDto.getLastName());
		user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
		return user;
	}

	private List<UserResponseDto> convertUserToUserResponseDto(List<User> content) {
		List<UserResponseDto> list = new ArrayList<UserResponseDto>();
		for (User user : content) {
			UserResponseDto userResponseDto = convertUserToUserResponseDto(user);
			System.out.println(userResponseDto);
			list.add(userResponseDto);
		}
		return list;
	}

	@Override
	public ContactResponseDto createAndUpdateContact(ContactRequestDto contactRequestDto) {
		if(findByUserNameAndActive()) {
			if(contactRequestDto.getContactId()==0) {
				Contact contact=convertContactRequestDtoToContact(contactRequestDto);
				return convertContactToContactResponseDto(contactRepository.save(contact));
			}
			Contact contact = contactRepository.findById(contactRequestDto.getContactId()).orElse(null);
			if(contact==null) {
				throw new NoContactNotFoundException("Contact with id "+contactRequestDto.getContactId()+" not found");
			}
			if(contactRequestDto.getFirstName()!=null && contactRequestDto.getFirstName()!="") {
				contact.setFirstName(contactRequestDto.getFirstName());
			}
			if(contactRequestDto.getLastName()!=null && contactRequestDto.getLastName()!="") {
				contact.setLastName(contactRequestDto.getLastName());
			}
			if(contactRequestDto.isActive() != contact.isActive()) {
				contact.setActive(contactRequestDto.isActive());
			}
		}
		return null;
	}

	private ContactResponseDto convertContactToContactResponseDto(Contact save) {
		ContactResponseDto responseDto=new ContactResponseDto();
		responseDto.setFirstName(save.getFirstName());
		responseDto.setLastName(save.getLastName());
		responseDto.setContactId(save.getContactId());
		responseDto.setUser(convertUserToUserResponseDto(save.getUser()));
		
		return responseDto;
	}

	private Contact convertContactRequestDtoToContact(ContactRequestDto contactRequestDto) {
		String userName = findByEmail();
		User user = userRepository.findByEmail(userName).orElse(null);
		Contact contact=new Contact();
		contact.setFirstName(contactRequestDto.getFirstName());
		contact.setLastName(contactRequestDto.getLastName());
		contact.setUser(user);
		contact.setActive(contactRequestDto.isActive());
		return contact;
	}

	private String findByEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}

	@Override
	public PagedResponse<ContactResponseDto> getAllContacts(int page, int size, String sortBy, String direction) {
		String email = findByEmail();
		User user = userRepository.findByEmail(email).orElse(null);
		Sort sort = Sort.by(sortBy);
		if (Sort.Direction.DESC.equals(direction)) {
			sort.descending();
		} else {
			sort.ascending();
		}
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		Page<Contact> contacts=contactRepository.findByUser(user,pageRequest);
		return new PagedResponse<>(convertContactToContactResponseDto(contacts.getContent()), contacts.getNumber(), contacts.getSize(), contacts.getTotalElements(), contacts.getTotalPages(), contacts.isLast());
	}

	private List<ContactResponseDto> convertContactToContactResponseDto(List<Contact> contacts) {
		List<ContactResponseDto> contactresponseDtos=new ArrayList<>();
		for(Contact contact:contacts) {
			contactresponseDtos.add(convertContactToContactResponseDtoWithoutUser(contact));
		}
		return contactresponseDtos;
	}

	private ContactResponseDto convertContactToContactResponseDtoWithoutUser(Contact contact) {
		ContactResponseDto responseDto=new ContactResponseDto();
		responseDto.setFirstName(contact.getFirstName());
		responseDto.setLastName(contact.getLastName());
		responseDto.setContactId(contact.getContactId());
		return responseDto;
	}
}
