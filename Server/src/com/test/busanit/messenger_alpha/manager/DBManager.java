package com.test.busanit.messenger_alpha.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.test.busanit.messenger_alpha.beans.Member;
import com.test.busanit.messenger_alpha.beans.Message;
import com.test.busanit.messenger_alpha.beans.Profile;

public class DBManager {
	
	private DBConnectionMgr pool;
	
	private FileManager fileManager;

	public DBManager() {
		pool = DBConnectionMgr.getInstance();
		fileManager = new FileManager();
	}

	public boolean checkId(String id, String pass) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		boolean flag = false;
		try {
			conn = pool.getConnection();

			sql = "select count(id) from tblmember where id=? and pass=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pass);
			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return flag;
	}

	public boolean insertMember(Member member) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		boolean flag = false;
		try {
			conn = pool.getConnection();
			// conn.setAutoCommit(false);

			sql = "insert into tblMember(id, pass, nick, img, p_num, id_open) values(?, ?, ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);

			String id = member.getId();
			String pass = member.getPass();
			String nick = member.getNick();

			String fileName = id + System.currentTimeMillis() + ".png";
			byte[] bitmapData = member.getBitmapData();

			String num = member.getNum();

			boolean isOpenId = member.isOpenId();

			pstmt.setString(1, id);
			pstmt.setString(2, pass);
			pstmt.setString(3, nick);

			if (bitmapData != null) {
				fileManager.saveToFile(fileName, bitmapData);
				pstmt.setString(4, fileName);
			} else {
				pstmt.setString(4, null);
			}
			pstmt.setString(5, num);

			if (isOpenId) {
				pstmt.setString(6, "1");
			} else {
				pstmt.setString(6, "0");
			}

			int cnt = pstmt.executeUpdate();

			if (cnt == 1) {
				flag = true;
			}

			createMemberTable(conn, pstmt, id);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return flag;
	}

	private void createMemberTable(Connection conn, PreparedStatement pstmt, String id) throws SQLException {
		String sql = "create table " + id + "_history(" + "toid varchar2(20) not null,"
				+ "tonick varchar2(20) not null," + "content varchar2(80)," + "img varchar2(40),"
				+ "senddate varchar2(40) not null)";

		pstmt = conn.prepareStatement(sql);
		pstmt.executeQuery();
	}

	public boolean insertFriend(String userId, String friendId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		boolean flag = false;
		try {
			conn = pool.getConnection();

			sql = "insert into tblFriendList(id, friend) values(?, ?)";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, friendId);

			int cnt = pstmt.executeUpdate();
			if (cnt == 1) {
				flag = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return flag;
	}

	public boolean insertMessage(Message message) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		String id = message.getFrom();
		boolean flag = false;
		try {
			conn = pool.getConnection();

			sql = "insert into " + id + "_history(toId, toNick, content, img, sendDate) values(?, ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);

			String toId = message.getTo();
			String toNick = message.getToNick();
			String content = message.getContent();
			byte[] bitmapData = message.getBitmapData();
			String sendDate = message.getDate();

			pstmt.setString(1, toId);
			pstmt.setString(2, toNick);
			pstmt.setString(3, content);

			String fileName = id + System.currentTimeMillis() + ".png";

			if (bitmapData != null) {
				fileManager.saveToFile(fileName, bitmapData);
				pstmt.setString(4, fileName);
			} else {
				pstmt.setString(4, null);
			}
			pstmt.setString(5, sendDate);

			int cnt = pstmt.executeUpdate();
			if (cnt == 1) {
				flag = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return flag;
	}

	public boolean removeFriend(String id, String friendId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			conn = pool.getConnection();
			sql = "delete from tblFriendList where id=? and friend=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, friendId);

			int cnt = pstmt.executeUpdate();
			if (cnt == 1)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return flag;
	}

	public boolean updateProfile(Profile profile) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = null;
		boolean flag = false;
		try {
			con = pool.getConnection();
			sql = "update tblmember set nick=?, img=?, msg=?, id_open=? where id=?";
			pstmt = con.prepareStatement(sql);

			String id = profile.getId();
			String nick = profile.getNick();
			byte[] bitmapData = profile.getBitmapData();

			String fileName = id + System.currentTimeMillis() + ".png";

			String msg = profile.getMsg();

			boolean isOpenId = profile.isOpenId();

			pstmt.setString(1, nick);

			if (bitmapData != null) {
				fileManager.saveToFile(fileName, bitmapData);
				pstmt.setString(2, fileName);
			} else {
				pstmt.setString(2, null);
			}
			pstmt.setString(3, msg);

			if (isOpenId) {
				pstmt.setString(4, "1");
			} else {
				pstmt.setString(4, "0");
			}

			pstmt.setString(5, id);

			int cnt = pstmt.executeUpdate();
			if (cnt == 1)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return flag;
	}

	public Member getMember(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		Member member = new Member();
		try {
			conn = pool.getConnection();

			sql = "select * from tblmember where id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				member.setId(rs.getString("ID"));
				member.setNick(rs.getString("NICK"));

				String fileName = rs.getString("IMG");

				byte[] bitmapData = fileManager.loadFromFile(fileName);
				if (bitmapData != null) {
					member.setBitmapData(bitmapData);
				}

				member.setMsg(rs.getString("MSG"));
				member.setNum(rs.getString("P_NUM"));

				String isOpenId = rs.getString("ID_OPEN");
				if (isOpenId.equals("1")) {
					member.setOpenId(true);
				} else {
					member.setOpenId(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return member;
	}

	public List<Member> getMemberList(String searchId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		List<Member> memberList = new ArrayList<>();
		try {
			conn = pool.getConnection();

			sql = "select * from tblmember where id like ? and id_open='1'";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + searchId + "%");

			rs = pstmt.executeQuery();
			while (rs.next()) {
				Member member = new Member();

				member.setId(rs.getString("ID"));
				member.setNick(rs.getString("NICK"));

				String fileName = rs.getString("IMG");

				byte[] bitmapData = fileManager.loadFromFile(fileName);
				member.setBitmapData(bitmapData);

				member.setMsg(rs.getString("MSG"));
				member.setNum(rs.getString("P_NUM"));

				String isOpenId = rs.getString("ID_OPEN");
				if (isOpenId.equals("1")) {
					member.setOpenId(true);
				} else {
					member.setOpenId(false);
				}
				memberList.add(member);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return memberList;
	}

	public byte[] getMemberImage(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		byte[] bitmapData = null;
		try {
			conn = pool.getConnection();

			sql = "select img from tblmember where id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			rs.next();

			String imageName = rs.getString("IMG");

			bitmapData = fileManager.loadFromFile(imageName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return bitmapData;

	}

	public boolean existId(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		boolean flag = false;
		try {
			conn = pool.getConnection();

			sql = "select count(id) from tblmember where id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return flag;
	}

	public boolean existFriend(String id, String friendId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		boolean flag = false;
		try {
			conn = pool.getConnection();

			sql = "select count(id) from tblFriendList where id=? and friend=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, friendId);
			rs = pstmt.executeQuery();

			if (rs.next() && rs.getInt(1) == 1) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return flag;
	}

	public String getMemberNick(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		String nick = null;
		try {
			conn = pool.getConnection();

			sql = "select nick from tblmember where id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			rs.next();

			nick = rs.getString("NICK");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return nick;

	}

	public List<String> getFriendIdList(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<String> friendIdLIst = new ArrayList<>();
		try {
			conn = pool.getConnection();

			sql = "select * from tblFriendList where id=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				String friendID = rs.getString("FRIEND");
				friendIdLIst.add(friendID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return friendIdLIst;
	}

	public List<Message> getChatHistory(String id, String friendId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;
		List<Message> chatHistory = new ArrayList<>();
		
		try {
			conn = pool.getConnection();

			byte[] userImgData = getMemberImage(id);
			byte[] friendImgData = getMemberImage(friendId);
			String userNick = getMemberNick(id);
			String friendNIck = getMemberNick(friendId);

			sql = "select * from(select * from " + id + "_history where toid='" + friendId + "' union select * from "
					+ friendId + "_history where toid='" + id + "') order by senddate";

			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery(sql);

			while (rs.next()) {
				String toId = rs.getString("TOID");
				String toNick = rs.getString("TONICK");
				String content = rs.getString("CONTENT");
				String fileName = rs.getString("IMG");
				String senddate = rs.getString("SENDDATE");
				
				Message message = new Message();

				if (toId.equals(id)) {
					message.setFrom(friendId);
					message.setFromNick(friendNIck);
					message.setFromImgData(friendImgData);
				} else {
					message.setFrom(id);
					message.setFromNick(userNick);
					message.setFromImgData(userImgData);
				}

				message.setTo(toId);
				message.setToNick(toNick);

				if (content != null) {
					message.setContent(content);
				}

				if (fileName != null) {
					byte[] bitmapData = fileManager.loadFromFile(fileName);
					message.setBitmapImage(bitmapData);
				}

				message.setDate(senddate);

				chatHistory.add(message);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return chatHistory;
	}
}