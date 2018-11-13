package ch.epfl.sweng.studyup.player;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.studyup.MainActivity;
import ch.epfl.sweng.studyup.firebase.Firestore;
import ch.epfl.sweng.studyup.items.Items;

import static ch.epfl.sweng.studyup.utils.Utils.*;
import static ch.epfl.sweng.studyup.utils.Constants.*;
import static ch.epfl.sweng.studyup.utils.GlobalAccessVariables.*;

import ch.epfl.sweng.studyup.utils.Constants;
import ch.epfl.sweng.studyup.utils.DataContainers.*;

/**
 * Player
 * <p>
 * Used to store the Player's state and informations.
 */
public class Player {

    private static final String TAG = Player.class.getSimpleName();

    private static Player instance = null;

    // Basic biographical data
    private String sciperNum;
    private String firstName;
    private String lastName;

    private String username;
    private Role role;

    // Game-related data
    private int experience;
    private int level;
    private int currency;

    private int[] questionsCurr;
    private int[] questsCurr;
    private int[] questionsAcheived;
    private int[] questsAcheived;
    private List<Items> items;

    private List<Course> courses;

    private Player() {
        experience = INITIAL_XP;
        currency = INITIAL_CURRENCY;
        level = INITIAL_LEVEL;
        username = INITIAL_USERNAME;
        items = new ArrayList<>();
        courses = new ArrayList<>();
        courses.add(Course.SWENG);
    }

    public static Player get() {
        if (instance == null) {
            Log.d(TAG, "PLAYER IS NULL");
            instance = new Player();
        }
        return instance;
    }

    /**
     * Initialize the instance of Player for the FIRST TIME.
     * This is used when a user logs is logged in from AuthenticationActivity OR
     * the user is logged in automatically from LoginActivity.
     */
    public void initializePlayerData(String sciperNum, String firstName, String lastName) {

        this.sciperNum = sciperNum;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Populates the player class with persisted data.
     * This method is called from FireStore.loadPlayerData(), which is called
     * in AuthenticationActivity.
     */
    public void updateLocalDataFromRemote(Map<String, Object> remotePlayerData) {

        if (remotePlayerData.isEmpty()) {
            Log.e(TAG,"Unable to retrieve player data from Firebase.");
            return;
        }

        username = getOrDefault(remotePlayerData, FB_USERNAME, INITIAL_USERNAME).toString();
        experience = Integer.parseInt(getOrDefault(remotePlayerData, FB_XP, INITIAL_XP).toString());
        currency = Integer.parseInt(getOrDefault(remotePlayerData, FB_CURRENCY, INITIAL_CURRENCY).toString());
        level = Integer.parseInt(getOrDefault(remotePlayerData, FB_LEVEL, INITIAL_LEVEL).toString());
        items = getItemsFromString((List<String>) getOrDefault(remotePlayerData, FB_ITEMS, new ArrayList<String>()));
    }

    // Getters
    public String getSciperNum() { return sciperNum; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Role getRole() { return this.role; }
    public String getUserName() { return username; }
    public int getExperience() { return experience; }
    public int getLevel() { return level; }
    public int getCurrency() { return currency; }
    public String getCurrentRoom() { return ROOM_NUM; }
    public List<Items> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }
    public List<String> getItemNames() {
        List<String> itemStringsList = new ArrayList<>();
        for (Items currItem : items) {
            itemStringsList.add(currItem.name());
        }
        return itemStringsList;
    }
    public double getLevelProgress() {
        return (experience % XP_TO_LEVEL_UP) * 1.0 / XP_TO_LEVEL_UP;
    }
    public List<Course> getCourses() {
        return courses;
    }

    // Setters
    public void setSciperNum(String sciperNum) {
        this.sciperNum = sciperNum;
        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    public void setFirstName(String firstName) {
        firstName = firstName;
        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setUserName(String newUsername) {
        username = newUsername;
        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    // Method suppose that we can only gain experience.
    private void updateLevel(Activity activity) {
        int newLevel = experience / XP_TO_LEVEL_UP + 1;

        if (newLevel - level > 0) {
            addCurrency((newLevel - level) * CURRENCY_PER_LEVEL, activity);
            level = newLevel;
        }

        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    public void addExperience(int xp, Activity activity) {
        experience += xp;
        updateLevel(activity);

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).updateXpAndLvlDisplay();
            ((MainActivity) activity).updateCurrDisplay();
            Log.i("Check", "Activity is " + activity.toString() + " " + ((MainActivity) activity).getLocalClassName());
        }

        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    public void addCurrency(int curr, Activity activity) {
        currency += curr;

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).updateCurrDisplay();
        }

        Firestore.get().updateRemotePlayerDataFromLocal();
    }
    public void addItem(Items item) {
        if (items.add(item)) {
            Firestore.get().updateRemotePlayerDataFromLocal();
        }
    }
    public void consumeItem(Items item) throws Exception {
        if (items.remove(item)) {
            item.consume();
            Firestore.get().updateRemotePlayerDataFromLocal();
        } else {
            throw new Exception("The player does not have this item, could not find it.");
        }
    }
    public void addCourse(Course newCourse) {
        courses.add(newCourse);
    }

    public boolean isInitialPlayer() throws NumberFormatException {
        boolean isInitial = firstName.equals(Constants.INITIAL_FIRSTNAME);
        isInitial = isInitial && lastName.equals(Constants.INITIAL_LASTNAME);
        isInitial = isInitial &&  Integer.parseInt(sciperNum) == Constants.INITIAL_SCIPER;

        return isInitial;
    }
}