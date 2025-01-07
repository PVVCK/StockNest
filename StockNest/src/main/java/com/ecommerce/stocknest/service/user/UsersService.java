package com.ecommerce.stocknest.service.user;

import java.util.List;

import com.ecommerce.stocknest.dto.AddUsersDTO;
import com.ecommerce.stocknest.dto.UsersDTO;
import com.ecommerce.stocknest.model.Users;

public interface UsersService {

	public UsersDTO getUserByID(Long usersId);
	
	public List<UsersDTO> getAllUsers();
	
	public UsersDTO createUser(AddUsersDTO userDTO);
	
	public UsersDTO updateUser(AddUsersDTO users, Long usersId);
	
	public void deleteUser(Long usersId);
	
}
