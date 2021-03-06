package com.struggle.obs.bbs.dao.impl;

import org.springframework.stereotype.Repository;

import com.struggle.obs.bbs.dao.UserDao;
import com.struggle.obs.bean.user.User;
import com.struggle.obs.formbean.UserForm;
import com.struggle.obs.syscom.bean.Page;
import com.struggle.obs.syscom.bean.QueryHelper;
import com.struggle.obs.syscom.dao.impl.GenericDaoImpl;
import com.struggle.obs.syscom.util.ConstantDefine;
import com.struggle.obs.syscom.util.Utils;

/**
 * @author tangyh 2014年8月19日 下午11:42:02
 */
@Repository
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

	@Override
	public User findAdminByPwd(User user) {
		return (User) getSession()
				.createQuery(//
						"FROM User u WHERE u.loginName=? and u.passWord=? and u.admin=?")
				.setParameter(0, user.getLoginName())
				.setParameter(1, user.getPassWord())
				.setParameter(2, user.isAdmin()).uniqueResult();
	}

	/**
	 * 2014年8月23日 上午9:59:27
	 * 
	 * @see com.struggle.obs.bbs.dao.UserDao#getUserCount()
	 */
	@Override
	public Integer getUserCount() {
		String hql = "SELECT COUNT(u.id) FROM User u ";
		Long count = (Long) getSession().createQuery(hql).uniqueResult();
		if (count != null) {
			return count.intValue();
		}
		return 0;
	}

	/**
	 * 2014年8月23日 上午9:59:27
	 * 
	 * @see com.struggle.obs.bbs.dao.UserDao#getNewUser()
	 */
	@Override
	public User getNewUser() {
		String hql = "FROM User u ORDER BY u.addDate DESC";
		return (User) getSession().createQuery(hql).setFirstResult(0)
				.setMaxResults(1).uniqueResult();

	}

	/**
	 * 2014年9月8日 下午9:05:51
	 * 
	 * @see com.struggle.obs.bbs.dao.UserDao#findByNoAudit(com.struggle.obs.formbean.UserForm)
	 */
	@Override
	public Page<User> findByNoAudit(Integer pageNum, Integer pageSize,
			Integer pages, UserForm userForm) {
		QueryHelper<User, Long> queryHelper = new QueryHelper<User, Long>(
				User.class, "u");
		queryHelper.addCondition("u.deleteFlag=?", ConstantDefine.NO_DEL_FLAG)//
				.addCondition("u.realNameVerify IS NOT NULL");
		// 条件：根据 技术方向/用户名/性别/出生地/居住地/状态 查找
		queryHelper.addCondition(
				!Utils.isEmptyOrNull(userForm.getdirectionCode()),
				"u.realNameVerify.directionCode=?",
				Utils.trim(userForm.getdirectionCode()));
		queryHelper.addCondition(!Utils.isEmptyOrNull(userForm.getLoginName()),
				"u.loginName like ?", "%" + Utils.trim(userForm.getLoginName())
						+ "%");
		queryHelper.addCondition(!Utils.isEmptyOrNull(userForm.getGender()),
				"u.realNameVerify.gender=?", userForm.getGender());
		queryHelper.addCondition(!Utils.isEmptyOrNull(userForm.getBirthCity()),
				"u.realNameVerify.birthCity like ?",
				"%" + Utils.trim(userForm.getBirthCity()) + "%");
		queryHelper.addCondition(
				!Utils.isEmptyOrNull(userForm.getResideCity()),
				"u.realNameVerify.resideCity like ?",
				"%" + Utils.trim(userForm.getResideCity()) + "%");
		queryHelper.addCondition(
				!Utils.isEmptyOrNull(userForm.getAuditStatu()),
				"u.realNameVerify.audit=?", userForm.getAuditStatu());
		// 排序：按照user认证时间升序
		queryHelper.addOrderProperty("u.realNameVerify.verifyTime", true);
		return queryHelper.preparePageBean(this, pageNum, pageSize, pages);
	}
}
