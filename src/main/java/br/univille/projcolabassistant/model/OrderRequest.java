package br.univille.projcolabassistant.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class OrderRequest extends AbstractReportObject {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date orderDate;
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date orderFinshDate;
	private int status;
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="orderstatuslog_id")
	private List<OrderStatusLog> orderStatusLogList = new ArrayList<OrderStatusLog>();
	@ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
	private Institution institution = new Institution();
	@ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
	private User user = new User();
	@ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
	private User userRequest = new User();
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="orderitems_id")
	private List<OrderItems> itensList = new ArrayList<OrderItems>();
	@Column(length=1000)
	private String description;
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OrderRequest() {
		this.setReportType("orders");
		this.setTemplatePath("report/order-pdf-template");
	}
	
	public OrderRequest(long id, Date orderDate, Date orderFinshDate, int status, Institution institution, User user,
			User userRequest) {
		this.id = id;
		this.orderDate = orderDate;
		this.orderFinshDate = orderFinshDate;
		this.status = status;
		this.institution = institution;
		this.user = user;
		this.userRequest = userRequest;
		
		this.setReportType("orders");
		this.setTemplatePath("report/order-pdf-template");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Date getOrderFinshDate() {
		return orderFinshDate;
	}

	public void setOrderFinshDate(Date orderFinshDate) {
		this.orderFinshDate = orderFinshDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<OrderStatusLog> getOrderStatusLogList() {
		return orderStatusLogList;
	}

	public void setOrderStatusLogList(List<OrderStatusLog> orderStatusLogList) {
		this.orderStatusLogList = orderStatusLogList;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(User userRequest) {
		this.userRequest = userRequest;
	}

	public List<OrderItems> getItensList() {
		return itensList;
	}

	public void setItensList(List<OrderItems> itensList) {
		this.itensList = itensList;
	}
}
