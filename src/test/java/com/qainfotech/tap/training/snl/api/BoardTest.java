package com.qainfotech.tap.training.snl.api;

import org.testng.annotations.Test;
import org.testng.annotations.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.*;

public class BoardTest {
	Board board;
	Board boad;

	@BeforeTest
	public void load() throws FileNotFoundException, UnsupportedEncodingException, IOException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption {
		board = new Board();
		board.registerPlayer("manu");
	}

	@Test(expectedExceptions = PlayerExistsException.class, priority = 1)
	public void PlayerExistsExceptionTest() throws FileNotFoundException, UnsupportedEncodingException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption, IOException {
		board.registerPlayer("manu");
	}

	@Test(expectedExceptions = MaxPlayersReachedExeption.class, priority = 2)
	public void MaxPlayersReachedExeptionTest() throws FileNotFoundException, UnsupportedEncodingException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption, IOException {
		board.registerPlayer("sid");
		board.registerPlayer("sumit");
		board.registerPlayer("nishant");
		board.registerPlayer("mohit");

	}

	@Test(expectedExceptions = GameInProgressException.class, priority = 4)
	public void GameInProgressExceptionTest() throws com.qainfotech.tap.training.snl.api.InvalidTurnException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption, IOException, NoUserWithSuchUUIDException { 
		UUID uuid = UUID
				.fromString(((JSONObject) board.getData().getJSONArray("players").get(3)).get("uuid").toString());
		board.deletePlayer(uuid);
		board.registerPlayer("kalu");
	}

	@Test(expectedExceptions = InvalidTurnException.class, priority = 3)
	public void InvalidTurnExceptionTest() throws FileNotFoundException, UnsupportedEncodingException,
			com.qainfotech.tap.training.snl.api.InvalidTurnException {
		UUID uuid = UUID
				.fromString(((JSONObject) board.getData().getJSONArray("players").get(0)).get("uuid").toString());
		board.rollDice(uuid);
		board.rollDice(uuid);
	}

	@Test(expectedExceptions = NoUserWithSuchUUIDException.class, priority = 5)
	public void NoUerWithSuchUUIDExceptionTest() throws FileNotFoundException, UnsupportedEncodingException,
			com.qainfotech.tap.training.snl.api.InvalidTurnException, NoUserWithSuchUUIDException {
		UUID uuid = UUID.randomUUID();
		board.deletePlayer(uuid);

	}

	@Test
	public void test1() throws FileNotFoundException, UnsupportedEncodingException, IOException,
			com.qainfotech.tap.training.snl.api.PlayerExistsException,
			com.qainfotech.tap.training.snl.api.GameInProgressException,
			com.qainfotech.tap.training.snl.api.MaxPlayersReachedExeption,
			com.qainfotech.tap.training.snl.api.InvalidTurnException {
		int dice = 0;
		boad = new Board();
		JSONArray steps = boad.data.getJSONArray("steps");
		steps.getJSONObject(1).put("target", 97);
		steps.getJSONObject(2).put("target", 97);
		steps.getJSONObject(3).put("target", 97);
		steps.getJSONObject(4).put("target", 97);
		steps.getJSONObject(5).put("target", 97);
		steps.getJSONObject(6).put("target", 97);
		steps.getJSONObject(99).put("target", 99);
		BoardModel.save(boad.uuid, boad.data);
		boad.registerPlayer("siddharth");
		UUID uuid = UUID
				.fromString(((JSONObject) boad.getData().getJSONArray("players").get(0)).get("uuid").toString());
		JSONObject response = boad.rollDice(uuid);

		do {
			response = boad.rollDice(uuid);
			dice = response.getInt("dice");
			if (dice >= 4) {
				assertThat(response.getString("message")).isEqualTo("Incorrect roll of dice. Player did not move");
				break;
			}
		} while (true);
	}

	@Test
	public void gameProcessTest() throws InvalidTurnException, PlayerExistsException, GameInProgressException,
			MaxPlayersReachedExeption, IOException {
		Board board1 = new Board();
		int position = 0;
		int type = 0;
		int turn = 0;
		board1.registerPlayer("sourav");
		board1.registerPlayer("gorav");
		int length = board1.data.getJSONArray("players").length();
		while (position < 100) {

			JSONObject student = ((JSONObject) board1.data.getJSONArray("players").get(turn));
			UUID uuid = UUID
					.fromString(((JSONObject) board1.data.getJSONArray("players").get(turn)).get("uuid").toString());
			position = ((JSONObject) board1.data.getJSONArray("players").get(turn)).getInt("position");
			System.out.println("1st position"+position);
			JSONObject response = board1.rollDice(uuid);
			int dice = response.getInt("dice");
			System.out.println(dice);
			position = position + dice;
			if (position <= 100)
				type = ((JSONObject) board1.data.getJSONArray("steps").get(position)).getInt("type");

			if (type == 0 && position <= 100) {
				assertThat(student.getInt("position")).isEqualTo(position);
                System.out.println(position);
			} else if (type == 1 && position <= 100) {

				position = ((JSONObject) board1.data.getJSONArray("steps").get(position)).getInt("target");
				assertThat(student.getInt("position")).isEqualTo(position);
				 System.out.println(position);
				// assertThat(actual)
			} else if (type == 2 && position <= 100) {
				position = ((JSONObject) board1.data.getJSONArray("steps").get(position)).getInt("target");
				assertThat(student.getInt("position")).isEqualTo(position);
				 System.out.println(position);
			}

			if (turn == length - 1)
				turn = 0;
			else
				turn = turn + 1;

		}

	}

	@Test(priority = 6)
	public void deletePlayer() throws FileNotFoundException, UnsupportedEncodingException, NoUserWithSuchUUIDException {
		String name = ((JSONObject) board.data.getJSONArray("players").get(0)).get("name").toString();
		UUID uuid = UUID.fromString(((JSONObject) board.data.getJSONArray("players").get(0)).get("uuid").toString());
		board.deletePlayer(uuid);
		assertThat(((JSONObject) board.data.getJSONArray("players").get(0)).getString("name")).isNotEqualTo(name);

	}
}