package com.monocept.myapp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.AccountResponseDto;
import com.monocept.myapp.dto.CustomerRequestDto;
import com.monocept.myapp.dto.CustomerResponseDto;
import com.monocept.myapp.dto.ProfileRequestDto;
import com.monocept.myapp.dto.TransactionResponseDto;
import com.monocept.myapp.dto.UserResponseDto;
import com.monocept.myapp.entity.Account;
import com.monocept.myapp.entity.Bank;
import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.Transaction;
import com.monocept.myapp.entity.TransactionType;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.NoRecordFoundException;
import com.monocept.myapp.repository.AccountRepository;
import com.monocept.myapp.repository.BankRepository;
import com.monocept.myapp.repository.CustomerRespository;
import com.monocept.myapp.repository.TransactionRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.EmailUtil;
import com.monocept.myapp.util.MailStructure;
import com.monocept.myapp.util.PDFUtil;
import com.monocept.myapp.util.PagedResponse;

import jakarta.mail.MessagingException;

@Service
public class BankApplicationServiceImpl implements BankApplicationService {
	@Value("${spring.mail.username}")
	private String fromMail;

	@Autowired
	private PDFUtil pdfUtil=new PDFUtil();
	@Autowired
	private EmailUtil emailUtil;
	private TransactionRepository transactionRepository;
	private CustomerRespository customerRespository;
	private AccountRepository accountRepository;
	private BankRepository bankRepository;
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	public BankApplicationServiceImpl(TransactionRepository transactionRepository,
			CustomerRespository customerRespository, AccountRepository accountRepository, BankRepository bankRepository,
			UserRepository userRepository, PasswordEncoder passwordEncoder) {
		super();
		this.transactionRepository = transactionRepository;
		this.customerRespository = customerRespository;
		this.accountRepository = accountRepository;
		this.bankRepository = bankRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	private List<TransactionResponseDto> convertTransactionToTransactionResponseDTO(List<Transaction> transactions) {
		List<TransactionResponseDto> transactionDtos = new ArrayList<TransactionResponseDto>();
		for (Transaction transaction : transactions) {
			transactionDtos.add(convertTransactionToTransactionResponseDTO(transaction));
		}
		return transactionDtos;
	}

	private TransactionResponseDto convertTransactionToTransactionResponseDTO(Transaction transaction) {
		TransactionResponseDto responseDto = new TransactionResponseDto();
		responseDto.setId(transaction.getId());
		if (transaction.getSenderAccount() != null) {
			responseDto.setSenderAccount(convertAccountTransactionToAccountResponseDto(transaction.getSenderAccount()));
		}
		if (transaction.getReceiverAccount() != null) {
			responseDto.setReceiverAccount(
					convertAccountTransactionToAccountResponseDto(transaction.getReceiverAccount()));
		}
		responseDto.setTransactionType(transaction.getTransactionType());
		responseDto.setTransactionDate(transaction.getTransactionDate());
		responseDto.setAmount(transaction.getAmount());
		return responseDto;
	}

	private AccountResponseDto convertAccountToAccountResponseDto(Account account) {
		AccountResponseDto accountResponseDTO = new AccountResponseDto();
		if (account != null) {
			accountResponseDTO.setAccountNumber(account.getAccountNumber());
			accountResponseDTO.setBalance(account.getBalance());
		}
		return accountResponseDTO;
	}

	private AccountResponseDto convertAccountTransactionToAccountResponseDto(Account account) {
		AccountResponseDto accountResponseDTO = new AccountResponseDto();
		if (account != null) {
			accountResponseDTO.setAccountNumber(account.getAccountNumber());
		}
		return accountResponseDTO;
	}

	@Override
	public UserResponseDto createCustomer(CustomerRequestDto customerRequestDto, long userID) {
		User user = userRepository.findById(userID).orElse(null);
		if (user == null) {
			throw new NoRecordFoundException("User not found with the following id " + userID);
		}
		if (user.getCustomer() != null) {
			throw new NoRecordFoundException("Customer already assigned cannot create another customer to the user");
		}
		Customer customer = convertCustomerRequestToCustomer(customerRequestDto);
		user.setCustomer(customer);
		String subject = "Welcome to Spring Bank Application! Your Customer ID Has Been Successfully Created";
		String emailBody = "Dear " + customerRequestDto.getFirstName() + " " + customerRequestDto.getLastName()
				+ ",\n\n" + "We are delighted to welcome you to the Spring Bank Application family!\n\n"
				+ "Your journey with us has just begun, and we're excited to be a part of your financial growth and success. We’re pleased to inform you that your Customer ID has been successfully created, and our team is now processing the final steps to set up your account. You can expect your account to be fully activated within the next few days.\n\n"
				+ "In the meantime, we encourage you to explore the various services and features that Spring Bank Application has to offer. From easy online banking to personalized customer support, we are here to make your banking experience as seamless and rewarding as possible.\n\n"
				+ "If you have any questions or need assistance, please don’t hesitate to reach out to our customer support team. We’re here to help every step of the way.\n\n"
				+ "Once again, welcome aboard! We look forward to serving you and supporting your financial goals.\n\n"
				+ "Warm regards,\n" + "Customer Relations Team\n" + "Spring Bank Application";
		MailStructure mailStructure = new MailStructure();
		mailStructure.setToEmail(user.getEmail());
		mailStructure.setEmailBody(emailBody);
		mailStructure.setSubject(subject);
		emailUtil.sendEmail(mailStructure);
		return convertUserToUserDto(userRepository.save(user));
	}

	private UserResponseDto convertUserToUserDto(User save) {
		UserResponseDto responseDto = new UserResponseDto();
		responseDto.setId(save.getId());
		responseDto.setCustomerResponseDto(convertCustomerToCustomerResponseDto(save.getCustomer()));
		responseDto.setEmail(save.getEmail());
		return responseDto;
	}

	private CustomerResponseDto convertCustomerToCustomerResponseDto(Customer save) {
		CustomerResponseDto customerResponseDto = new CustomerResponseDto();
		customerResponseDto.setCustomer_id(save.getCustomer_id());
		customerResponseDto.setFirstName(save.getFirstName());
		customerResponseDto.setLastName(save.getLastName());
		customerResponseDto.setTotalBalance(save.getTotalBalance());
		return customerResponseDto;
	}

	private Customer convertCustomerRequestToCustomer(CustomerRequestDto customerRequestDto) {
		Customer customer = new Customer();
		customer.setFirstName(customerRequestDto.getFirstName());
		customer.setLastName(customerRequestDto.getLastName());
		customer.setTotalBalance(customerRequestDto.getTotalBalance());
//		customer.setAccounts(convertAccountRequestDtoToAccount(customerRequestDto.getAccounts()));
		return customer;
	}

	@Override
	public CustomerResponseDto createAccount(long customerID, int bankID) {
		Customer customer = customerRespository.findById(customerID).orElse(null);
		if (customer == null) {
			throw new NoRecordFoundException("Customer with id " + customerID + " is not found");
		}
		Bank bank = bankRepository.findById(bankID).orElse(null);
		if (bank == null) {
			throw new NoRecordFoundException("Bank with id " + bankID + " is not found");
		}
		Account account = new Account();
		account.setBalance(1000);
		account.setCustomer(customer);
		account.setBank(bank);
		customer.addAccount(account);
		double totalSalary = 1000;
		if (accountRepository.getTotalBalance(customer) != 0) {
			totalSalary += accountRepository.getTotalBalance(customer);
		}
		User user = customer.getUser();
		customer.setTotalBalance(totalSalary);
		customerRespository.save(customer);
		String subject = "Your Account Has Been Successfully Created at Spring Bank Application!";
		String emailBody = "Dear " + customer.getFirstName() + " " + customer.getLastName() + ",\n\n"
				+ "We are pleased to inform you that your account has been successfully created with us!\n\n"
				+ "Your Customer ID is " + customer.getCustomer_id()
				+ ". You can view all the details of your account by logging into our application and sending a request to get customer details by your ID.\n\n"
				+ "If you need any assistance or have any questions, our support team is ready to help. We are committed to providing you with the best banking experience.\n\n"
				+ "Thank you for choosing Spring Bank Application. We look forward to supporting your financial needs.\n\n"
				+ "Best regards,\n" + "Customer Relations Team\n" + "Spring Bank Application";
		MailStructure mailStructure = new MailStructure();
		mailStructure.setToEmail(user.getEmail());
		mailStructure.setEmailBody(emailBody);
		mailStructure.setSubject(subject);
		emailUtil.sendEmail(mailStructure);

		return convertCustomerAccountToCustomerResponseDto(customer);
	}

	private CustomerResponseDto convertCustomerAccountToCustomerResponseDto(Customer customer) {
		CustomerResponseDto customerResponseDto = new CustomerResponseDto();
		customerResponseDto.setFirstName(customer.getFirstName());
		customerResponseDto.setLastName(customer.getLastName());
		customerResponseDto.setCustomer_id(customer.getCustomer_id());
		customerResponseDto.setAccounts(convertAccountToAccountResponseDto(customer.getAccounts()));
		customerResponseDto.setTotalBalance(customer.getTotalBalance());

		return customerResponseDto;
	}

	private List<AccountResponseDto> convertAccountToAccountResponseDto(List<Account> accounts) {
		List<AccountResponseDto> accountResponseDTOs = new ArrayList<>();
		for (Account account : accounts) {
			accountResponseDTOs.add(convertAccountToAccountResponseDto(account));
		}
		return accountResponseDTOs;
	}

	@Override
	public List<CustomerResponseDto> getAllCustomers() {
		return convertCustomerToCustomerResponseDto(customerRespository.findAll());
	}

	private List<CustomerResponseDto> convertCustomerToCustomerResponseDto(List<Customer> customers) {
		List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
		for (Customer customer : customers) {
			CustomerResponseDto customerResponseDto = new CustomerResponseDto();
			customerResponseDto.setCustomer_id(customer.getCustomer_id());
			customerResponseDto.setFirstName(customer.getFirstName());
			customerResponseDto.setLastName(customer.getLastName());
			customerResponseDto.setTotalBalance(customer.getTotalBalance());
			customerResponseDto.setAccounts(convertAccountToAccountResponseDto(customer.getAccounts()));
			customerResponseDtos.add(customerResponseDto);
		}
		return customerResponseDtos;
	}

	@Override
	public CustomerResponseDto getCustomerById(long customerid) {
		Customer customer = customerRespository.findById(customerid).orElse(null);
		if (customer == null) {
			throw new NoRecordFoundException("No customer found with the id " + customerid);
		}
		return convertCustomerAccountToCustomerResponseDto(customer);
	}

	@Override
	public PagedResponse<TransactionResponseDto> getAllTransactions(LocalDateTime fromDate, LocalDateTime toDate,
			int page, int size, String sortBy, String direction) {
		Sort sort = Sort.by(sortBy);
		if (direction.equalsIgnoreCase("desc")) {
			sort = sort.descending();
		} else {
			sort = sort.ascending();
		}
//		fromDate = fromDate.truncatedTo(ChronoUnit.SECONDS);
//		toDate = toDate.truncatedTo(ChronoUnit.SECONDS);
		PageRequest pageRequest = PageRequest.of(page, size, sort);
		System.out.println("Page request: " + pageRequest);
		Page<Transaction> pagedResponse = transactionRepository.findAllByTransactionDateBetween(fromDate, toDate,
				pageRequest);

		PagedResponse<TransactionResponseDto> response = new PagedResponse<>(
				convertTransactionToTransactionResponseDTO(pagedResponse.getContent()), pagedResponse.getNumber(),
				pagedResponse.getSize(), pagedResponse.getTotalElements(), pagedResponse.getTotalPages(),
				pagedResponse.isLast());
		return response;
	}

	@Override
	public TransactionResponseDto performTransaction(long senderAccountNumber, long receiverAccountNumber,
			double amount) {
		Optional<User> user = userRepository.findByEmail(getUsernameFromSecurityContext());
		List<Account> accounts = user.get().getCustomer().getAccounts();
		for (Account account : accounts) {
			if (account.getAccountNumber() == senderAccountNumber) {
				Account senderAccount = accountRepository.findById(senderAccountNumber).orElse(null);
				Account receiverAccount = accountRepository.findById(receiverAccountNumber).orElse(null);
				if (senderAccount == null || receiverAccount == null) {
					throw new NoRecordFoundException("Please check the sender account number " + senderAccountNumber
							+ " and receiver account number " + receiverAccountNumber);
				}
				if (senderAccount.equals(receiverAccount)) {
					throw new NoRecordFoundException("self transfer to the same account number not possible");
				}
				if (senderAccount.getBalance() < amount) {
					throw new NoRecordFoundException("Insufficient Funds please check the balance again");
				}
				senderAccount.setBalance(senderAccount.getBalance() - amount);
				receiverAccount.setBalance(receiverAccount.getBalance() + amount);
				accountRepository.save(senderAccount);
				accountRepository.save(receiverAccount);
				Customer senderCustomer = senderAccount.getCustomer();
				Customer receiverCustomer = receiverAccount.getCustomer();
				senderCustomer.setTotalBalance(senderCustomer.getTotalBalance() - amount);
				receiverCustomer.setTotalBalance(receiverCustomer.getTotalBalance() + amount);

				customerRespository.save(senderCustomer);
				customerRespository.save(receiverCustomer);
				Transaction transaction = new Transaction();
				transaction.setAmount(amount);
				transaction.setSenderAccount(senderAccount);
				transaction.setReceiverAccount(receiverAccount);
				transaction.setTransactionType(TransactionType.Transfer);
				User senderUser = senderCustomer.getUser();
				User receiverUser = receiverCustomer.getUser();
				sendMailToTheUsers(senderUser, receiverUser, senderCustomer, senderAccountNumber, transaction,
						receiverCustomer, receiverAccountNumber);

				return convertTransactionToTransactionResponseDTO(transactionRepository.save(transaction));
			}
		}
		throw new NoRecordFoundException("Your account number is wrong");

	}

	private void sendMailToTheUsers(User senderUser, User receiverUser, Customer senderCustomer,
			long senderAccountNumber, Transaction transaction, Customer receiverCustomer, long receiverAccountNumber) {

		String debitedsubject = "Notification: Your Account Has Been Debited at Spring Bank Application!";
		String debitedBody = "Dear " + senderCustomer.getFirstName() + " " + senderCustomer.getLastName() + ",\n\n"
				+ "We want to inform you that your account has been debited by an amount of " + transaction.getAmount()
				+ " on " + transaction.getTransactionDate() + ".\n\n" + "Account Number: ######" + senderAccountNumber
				+ "\n\n"
				+ "If you did not authorize this transaction or have any concerns about it, please contact our support team immediately. We are here to assist you and ensure your account's security.\n\n"
				+ "You can view the details of this transaction by logging into our application and checking your account transactions.\n\n"
				+ "Thank you for banking with Spring Bank Application. We are dedicated to supporting your financial needs.\n\n"
				+ "Best regards,\n" + "Customer Relations Team\n" + "Spring Bank Application";
		MailStructure mailStructure = new MailStructure();
		mailStructure.setToEmail(senderUser.getEmail());
		mailStructure.setEmailBody(debitedBody);
		mailStructure.setSubject(debitedsubject);
		emailUtil.sendEmail(mailStructure);
		String creditedsubject = "Notification: Your Account Has Been Credited at Spring Bank Application!";
		String creditedBody = "Dear " + receiverCustomer.getFirstName() + " " + receiverCustomer.getLastName() + ",\n\n"
				+ "We are pleased to notify you that your account has been credited with an amount of "
				+ transaction.getAmount() + " on " + transaction.getTransactionDate() + ".\n\n"
				+ "Account Number: ######" + receiverAccountNumber + "\n\n"
				+ "You can view the details of this transaction by logging into our application and checking your account transactions.\n\n"
				+ "If you have any questions or need further assistance, please contact our support team. We are here to ensure you have the best banking experience.\n\n"
				+ "Thank you for banking with Spring Bank Application. We look forward to continuing to support your financial needs.\n\n"
				+ "Best regards,\n" + "Customer Relations Team\n" + "Spring Bank Application";
		mailStructure = new MailStructure();
		mailStructure.setToEmail(receiverUser.getEmail());
		mailStructure.setEmailBody(creditedBody);
		mailStructure.setSubject(creditedsubject);
		emailUtil.sendEmail(mailStructure);

	}

	@Override
	public PagedResponse<TransactionResponseDto> getPassbook(long accountNumber, LocalDateTime from, LocalDateTime to,
			int page, int size, String sortBy, String direction)
			throws DocumentException, IOException, MessagingException {
		Sort sort = Sort.by(sortBy);
		if (direction.equalsIgnoreCase(Sort.Direction.DESC.name())) {
			sort.descending();
		} else {
			sort.ascending();
		}

		String email = getUsernameFromSecurityContext();
		Optional<User> user = userRepository.findByEmail(email);
		if (user.get().getCustomer() == null) {
			throw new NoRecordFoundException("still you havn't have customer id");
		}
		List<Account> accounts = user.get().getCustomer().getAccounts();
		for (Account acc : accounts) {
			if (acc.getAccountNumber() == accountNumber) {
				Account account = accountRepository.findById(accountNumber).orElse(null);
//				if (account == null) {
//					throw new NoRecordFoundException(
//							"No account found with the account number " + accountNumber + " Please check again");
//				}
				PageRequest pageRequest = PageRequest.of(page, size, sort);
				Page<Transaction> pagedResponse = transactionRepository.getPassbook(account, from, to, pageRequest);
				String passbookSubject = "Your Passbook from Spring Bank Application";
				String passbookBody = "Dear " + user.get().getCustomer().getFirstName() + " "
						+ user.get().getCustomer().getLastName() + ",\n\n"
						+ "Attached to this email is your passbook for your recent transactions.\n\n"
						+ "You can review the details of your transactions for the specified period. If you have any questions or need further assistance, please do not hesitate to reach out to us.\n\n"
						+ "Thank you for choosing Spring Bank Application. We are committed to providing you with the best service possible.\n\n"
						+ "Best regards,\n" + "Customer Relations Team\n" + "Spring Bank Application";

				MailStructure mailStructure = new MailStructure();
				mailStructure.setToEmail(user.get().getEmail());
				mailStructure.setEmailBody(passbookBody);
				mailStructure.setSubject(passbookSubject);
				List<TransactionResponseDto> responseDTO = convertTransactionToTransactionResponseDTO(pagedResponse.getContent(),accountNumber);
				byte[] passbookPdf = pdfUtil
						.generatePassbookPdf(user,responseDTO);
				emailUtil.sendEmailWithAttachment(mailStructure, passbookPdf);

				return new PagedResponse<TransactionResponseDto>(responseDTO,
						pagedResponse.getNumber(), pagedResponse.getSize(), pagedResponse.getTotalElements(),
						pagedResponse.getTotalPages(), pagedResponse.isLast());
			}
		}
		throw new NoRecordFoundException("Please give valid account number");

	}

	private List<TransactionResponseDto> convertTransactionToTransactionResponseDTO(List<Transaction> passbook,
			long accountNumber) {
		List<TransactionResponseDto> list = new ArrayList<>();
		for (Transaction transaction : passbook) {
			TransactionResponseDto responseDto = new TransactionResponseDto();
			responseDto.setAmount(transaction.getAmount());
			if (transaction.getReceiverAccount() != null) {
				responseDto.setReceiverAccount(
						convertAccountTransactionToAccountResponseDto(transaction.getReceiverAccount()));
			}
			if (transaction.getSenderAccount() != null) {
				responseDto.setSenderAccount(
						convertAccountTransactionToAccountResponseDto(transaction.getSenderAccount()));
			}
			responseDto.setId(transaction.getId());
			responseDto.setTransactionDate(transaction.getTransactionDate());
			if (transaction.getSenderAccount() != null
					&& transaction.getSenderAccount().getAccountNumber() == accountNumber) {
				responseDto.setTransactionType(TransactionType.Debited);
			} else {
				responseDto.setTransactionType(TransactionType.Credited);
			}
			list.add(responseDto);
		}
		return list;
	}

	@Override
	public String updateProfile(ProfileRequestDto profileRequestDto) {
		User user = userRepository.findByEmail(getUsernameFromSecurityContext()).orElse(null);
		if (user.getCustomer() == null) {
			throw new NoRecordFoundException("Cannot update the customer details still you havn't have customer id");
		}
		Customer customer = user.getCustomer();
		if (profileRequestDto.getEmail() != null && !profileRequestDto.getEmail().isEmpty()
				&& profileRequestDto.getEmail().length() != 0) {
			user.setEmail(profileRequestDto.getEmail());
		}
		if (profileRequestDto.getFirstName() != null && !profileRequestDto.getFirstName().isEmpty()
				&& profileRequestDto.getFirstName().length() != 0) {
			customer.setFirstName(profileRequestDto.getFirstName());
		}
		if (profileRequestDto.getLastName() != null && !profileRequestDto.getLastName().isEmpty()
				&& profileRequestDto.getLastName().length() != 0) {
			customer.setLastName(profileRequestDto.getLastName());
		}
		if (profileRequestDto.getPassword() != null && !profileRequestDto.getPassword().isEmpty()
				&& profileRequestDto.getPassword().length() != 0) {
			user.setPassword(passwordEncoder.encode(profileRequestDto.getPassword()));
		}

		userRepository.save(user);

		return "user succesfully updated";
	}

	private String getUsernameFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}

	@Override
	public AccountResponseDto depositAmount(long accountNumber, double amount) {
		User user = userRepository.findByEmail(getUsernameFromSecurityContext()).orElse(null);
		List<Account> accounts = user.getCustomer().getAccounts();
		Customer customer = user.getCustomer();
		if (customer == null) {
			throw new NoRecordFoundException("Customer not associated with the user");
		}
		for (Account account : accounts) {
			if (account.getAccountNumber() == accountNumber) {
				account.setBalance(account.getBalance() + amount);
				accountRepository.save(account);
				Double totalBalance = accountRepository.getTotalBalance(customer);
				customer.setTotalBalance(totalBalance);
				customerRespository.save(customer);
				Transaction transaction = new Transaction();
				transaction.setAmount(amount);
				transaction.setReceiverAccount(account);
				transaction.setTransactionType(TransactionType.Transfer);
				transactionRepository.save(transaction);
				String subject = "Notification: Your Account Has Been Credited at Spring Bank Application!";
				String emailBody = "Dear " + customer.getFirstName() + " " + customer.getLastName() + ",\n\n"
						+ "We are pleased to notify you that your account has been credited with an amount of "
						+ transaction.getAmount() + " on " + LocalDateTime.now() + ".\n\n" + "Account Number: ######"
						+ accountNumber + "\n\n"
						+ "You can view the details of this transaction by logging into our application and checking your account transactions.\n\n"
						+ "If you have any questions or need further assistance, please contact our support team. We are here to ensure you have the best banking experience.\n\n"
						+ "Thank you for banking with Spring Bank Application. We look forward to continuing to support your financial needs.\n\n"
						+ "Best regards,\n" + "Customer Relations Team\n" + "Spring Bank Application";

				MailStructure mailStructure = new MailStructure();
				mailStructure.setToEmail(user.getEmail());
				mailStructure.setEmailBody(emailBody);
				mailStructure.setSubject(subject);
				emailUtil.sendEmail(mailStructure);
				return convertAccountToAccountResponseDto(account);

			}
		}
		throw new NoRecordFoundException("Please check account number properly");

	}

	@Override
	public List<AccountResponseDto> getAccounts() {
		User user = userRepository.findByEmail(getUsernameFromSecurityContext()).orElse(null);
		return convertAccountToAccountResponseDto(user.getCustomer().getAccounts());
	}

	@Override
	public String deleteCustomer(long customerID) {
		Customer customer = customerRespository.findById(customerID).orElse(null);
		if (customer == null) {
			throw new NoRecordFoundException("Customer not found with the id " + customerID);
		}
		if (!customer.isActive()) {
			throw new NoRecordFoundException("Customer is already deleted");
		}
		customer.setActive(false);
		List<Account> accounts = customer.getAccounts();
		for (Account account : accounts) {
			account.setActive(false);
		}
		customerRespository.save(customer);
		return "Customer deleted successfully";
	}

	@Override
	public String activateCustomer(long customerID) {
		Customer customer = customerRespository.findById(customerID).orElse(null);
		if (customer == null) {
			throw new NoRecordFoundException("Customer not found with the id " + customerID);
		}
		if (customer.isActive()) {
			throw new NoRecordFoundException("Customer is already active");
		}
		customer.setActive(true);
		customerRespository.save(customer);
		return "Customer activated successfully";
	}

	@Override
	public String deleteAccount(long accountNumber) {
		Account account = accountRepository.findById(accountNumber).orElse(null);
		if (account == null) {
			throw new NoRecordFoundException("Account not found with the account number " + accountNumber);
		}
		if (!account.isActive()) {
			throw new NoRecordFoundException("Account is already deleted");
		}
		account.setActive(false);
		accountRepository.save(account);
		return "Account deleted successfully";
	}

	@Override
	public String activateAccount(long accountNumber) {
		Account account = accountRepository.findById(accountNumber).orElse(null);
		if (account == null) {
			throw new NoRecordFoundException("Account not found with the account number " + accountNumber);
		}
		if (account.isActive()) {
			throw new NoRecordFoundException("Account is already active");
		}
		account.setActive(true);
		accountRepository.save(account);
		return "Account activated successfully";
	}

	@Override
	public AccountResponseDto viewBalance(long accountNumber) {
		String email = getUsernameFromSecurityContext();
		Optional<User> user = userRepository.findByEmail(email);
		List<Account> accounts = user.get().getCustomer().getAccounts();
		for (Account account : accounts) {
			if (account.getAccountNumber() == accountNumber && isAccountActive(account)) {
				return convertAccountToAccountResponseDto(account);
			}
		}
		throw new NoRecordFoundException("Please check the account number");
	}

	private boolean isAccountActive(Account account) {
		if (!account.isActive()) {
			return false;
		}
		return true;
	}

}
