package com.monocept.myapp.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long contactId;
	
	@NotBlank
	private String firstName;
	
	@NotBlank
	private String lastName;
	
	@NotNull
	private boolean active=true;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "contact")
	private List<ContactDetail> contactDetails;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public void addContactDetail(ContactDetail contactDetail) {

		contactDetails.add(contactDetail);
	}

//	public void updateContactDetail(ContactDetail contactDetail) {
//		for(ContactDetail contactdetail:contactDetails) {
//			if(contactdetail.getContactDetailsId()==contactDetail.getContactDetailsId()) {
//				contactdetail.setContactType(contactdetail.getContactType());
//			}
//		}
//		
//	}
}
