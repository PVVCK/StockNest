package com.ecommerce.stocknest.service.user;

import java.util.List;

import com.ecommerce.stocknest.dto.AddUsersDTO;
import com.ecommerce.stocknest.dto.FetchUsersDTO;
import com.ecommerce.stocknest.dto.UsersDTO;
import com.ecommerce.stocknest.model.Cart;
import com.ecommerce.stocknest.model.CartItem;
import com.ecommerce.stocknest.model.Users;

public interface UsersService {

	public UsersDTO getUserByID(Long usersId);
	
	public Users findUserByUserName(String userName);
	
	public List<FetchUsersDTO> getAllUsers();
	
	public UsersDTO createUser(AddUsersDTO userDTO);
	
	public UsersDTO updateUser(AddUsersDTO users, Long usersId);
	
	public void deleteUser(Long usersId);
	
	 public Cart createCartForUser(Long userId);
	 
	 public CartItem addCartItemFromUser(Long userId, Long productId, int quantity);
	 
	 public String userPlacingOrder(Long userId, Long cartId);
	 
	 
	
}
