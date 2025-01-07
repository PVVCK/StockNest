package com.ecommerce.stocknest.service.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.stocknest.dto.AddUsersDTO;
import com.ecommerce.stocknest.dto.UsersDTO;
import com.ecommerce.stocknest.model.Users;
import com.ecommerce.stocknest.repository.UserRepository;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public UsersDTO createUser(AddUsersDTO addUserDTO) {
		// TODO Auto-generated method stub
		Users user = modelMapper.map(addUserDTO, Users.class);

		userRepository.save(user);
		
		return modelMapper.map(user, UsersDTO.class);
	}

	@Override
	public UsersDTO updateUser(AddUsersDTO users, Long usersId) {
		// TODO Auto-generated method stub
		 Users existingUser = userRepository.findById(usersId)
		            .orElseThrow(() -> new NoSuchElementException("User with Id :- " + usersId + " is not found"));
		    
		    
		    if (users.getUsername() != null) {
		        existingUser.setUsername(users.getUsername());
		    }
		    if (users.getEmail() != null) {
		        existingUser.setEmail(users.getEmail());
		    }
		    if (users.getPhoneNumber() != null) {
		        existingUser.setPhoneNumber(users.getPhoneNumber());
		    }
		    
		    Users updatedUser = userRepository.save(existingUser);
		    
		   
		    return modelMapper.map(updatedUser, UsersDTO.class);
	}

	@Override
	public UsersDTO getUserByID(Long usersId) {
		// TODO Auto-generated method stub
		Users users = userRepository.findById(usersId)
				.orElseThrow(() -> new NoSuchElementException("User with Id :- "+usersId+" is not found"));
		
		return modelMapper.map(users, UsersDTO.class);
	}

	@Override
	public List<UsersDTO> getAllUsers() {
		// TODO Auto-generated method stub
		List<Users> usersList = userRepository.findAll();

		if (usersList.isEmpty()) {
	        throw new NoSuchElementException("No Users are available in the database.");
	    }
	    // Use ModelMapper to convert the list of Users to a list of UsersDTO
	    List<UsersDTO> usersDTOList = usersList.stream()
	            .map(user -> modelMapper.map(user, UsersDTO.class))
	            .toList();

	    return usersDTOList;
	}

	public void deleteUser(Long usersId) {
		// TODO Auto-generated method stub
		userRepository.findById(usersId)
		.ifPresentOrElse(userRepository::delete,
				() -> {throw new NoSuchElementException("User with Id:- " +usersId +" is not Present");});
	}

}
