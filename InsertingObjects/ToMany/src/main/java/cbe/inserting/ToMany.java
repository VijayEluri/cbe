package cbe.inserting;

import java.util.Random;

import org.apache.cayenne.access.DataContext;

import cbe.inserting.constants.RoleType;
import cbe.inserting.model.Address;
import cbe.inserting.model.Setting;
import cbe.inserting.model.User;
import cbe.inserting.utilities.Populator;

/**
 * This example creates a to-many relationship (User ->> Settings).
 *
 * @author mrg
 */
public class ToMany
{
    public static void main(String[] arguments)
    {
        // Create a new DataContext. This will also initialize the Cayenne
        // Framework.
        DataContext dataContext = DataContext.createDataContext();

        // Create a new User object tracked by the DataContext.
        User user = dataContext.newObject(User.class);

        // Set values for the new user. In this case, we are initializing
        // an administrator.
        user.setFirstName("System");
        user.setLastName("Administrator");
        user.setUsername("admin");
        user.setPassword("admin123");
        user.setEnabled(true);
        user.setRole(RoleType.ADMIN);

        // Loop over all the names in our resources file and create users
        // for each of them.
        for (String firstName : Populator.getFirstNames())
            for (String lastName : Populator.getLastNames())
                createUser(dataContext, firstName, lastName);

        // Commit the changes to the database.  This includes User
        // and Setting records.
        dataContext.commitChanges();
    }

    /**
     * Helper method to create and initialize a user in a DataContext.
     *
     * @param dataContext The DataContext to register the user.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     */
    private static void createUser(DataContext dataContext, String firstName, String lastName)
    {
        // Create a new User object tracked by the DataContext.
        User user = dataContext.newObject(User.class);

        // Set values for the new user. Defaults the password to the username
        // with "123" appended.
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername((firstName.substring(0, 1) + lastName).toLowerCase());
        user.setPassword(user.getUsername() + "123");

        // Don't enable accounts whose last name starts with an "A".
        if (lastName.startsWith("A"))
            user.setEnabled(false);
        else
            user.setEnabled(true);

        // Accounts whose last name starts with a "B" are moderators.
        if (lastName.startsWith("B"))
            user.setRole(RoleType.MODERATOR);
        else
            user.setRole(RoleType.CUSTOMER);

        // Create an address for this user.
        createAddress(user);

        // Create settings for this user.
        createSettings(user);
    }

    /**
     * Creates an address for a user.
     *
     * @param user The user to create the address.
     */
    private static void createAddress(User user)
    {
        // Extract the DataContext from the user.  We are creating a
        // relationship and Cayenne requires that related objects reside
        // in the same DataContext, so be sure to use the same one.
        DataContext dataContext = (DataContext) user.getObjectContext();

        // Create the new address in the user's DataContext.
        Address address = dataContext.newObject(Address.class);

        // Set the address attributes.
        address.setCity("Falls Church");
        address.setState("VA");
        address.setStreet(getRandomStreet());
        address.setZip("22046");

        // Assign the address to the user.  Cayenne will figure out how
        // to map the relationship based upon the model and also create
        // the primary keys and foreign keys.
        user.setAddress(address);
    }

    /**
     * Create settings for the give user.
     * 
     * @param user
     */
    private static void createSettings(User user)
    {
        // Extract the DataContext from the user.  We are creating a
        // relationship and Cayenne requires that related objects reside
        // in the same DataContext, so be sure to use the same one.
        DataContext dataContext = (DataContext) user.getObjectContext();

        // Create some settings.
        Setting fontSize              = dataContext.newObject(Setting.class);
        Setting passwordResetQuestion = dataContext.newObject(Setting.class);
        Setting passwordResetAnswer   = dataContext.newObject(Setting.class);

        // Set random values for the settings.
        setRandomFontSize(fontSize);
        setRandomPasswordResetQuestionAndAnswer
        	(passwordResetQuestion, passwordResetAnswer);
        
        // Add the settings to the user.  Cayenne will figure out how
        // to map the relationship based upon the model and also create
        // the primary keys and foreign keys.
        user.addToSettings(fontSize);
        user.addToSettings(passwordResetQuestion);
        user.addToSettings(passwordResetAnswer);
    }

    /**
     * @return A made-up street address.
     */
    private static String getRandomStreet()
    {
        int    number = new Random().nextInt(1000) + 1;
        String street;

        switch (new Random().nextInt(5))
        {
            case 0: street = "W Broad Street"; break;
            case 1: street = "N Washington Street"; break;
            case 2: street = "N West Street"; break;
            case 3: street = "Great Falls Street"; break;
            case 4: street = "Annandale Road"; break;
            default: street = "Lincoln Avenue";
        }

        return number + " " + street;
    }
    
    /**
     * Set a random font size setting.
     * 
     * @param fontSize
     */
    private static void setRandomFontSize(Setting fontSize)
    {
        switch (new Random().nextInt(3))
        {
            case 0:
                fontSize.setKey("fontSize");
                fontSize.setValue("small");
                break;
            case 1:
                fontSize.setKey("fontSize");
                fontSize.setValue("medium");
                break;
            case 2:
                fontSize.setKey("fontSize");
                fontSize.setValue("large");
                break;
        }
    }

	/**
	 * Set a random password reset question.
	 * 
	 * @param passwordResetQuestion
	 */
	private static void setRandomPasswordResetQuestionAndAnswer
		(Setting passwordResetQuestion, Setting passwordResetAnswer)
    {
        switch (new Random().nextInt(3))
        {
            case 0:
                passwordResetQuestion.setKey("passwordResetQuestion");
                passwordResetQuestion.setValue("What is your favorite color?");
                passwordResetAnswer.setKey("passwordResetAnswer");
                passwordResetAnswer.setValue("red");
                break;
            case 1:
                passwordResetQuestion.setKey("passwordResetQuestion");
                passwordResetQuestion.setValue("What is your favorite activity?");
                passwordResetAnswer.setKey("passwordResetAnswer");
                passwordResetAnswer.setValue("biking");
                break;
            case 2:
                passwordResetQuestion.setKey("passwordResetQuestion");
                passwordResetQuestion.setValue("What is your favorite breakfast?");
                passwordResetAnswer.setKey("passwordResetAnswer");
                passwordResetAnswer.setValue("eggs");
        }
    }
}