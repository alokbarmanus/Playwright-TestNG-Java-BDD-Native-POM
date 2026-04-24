package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import com.microsoft.playwright.Locator;

/**
 * Page Object for the OrangeHRM Dashboard page.
 * URL: https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index
 */
public class DashboardPageObjects extends BasePage {

    // -------------------------------------------------------------------------
    // Locators — page header
    // -------------------------------------------------------------------------
    private final Locator dashboardHeader;

    // -------------------------------------------------------------------------
    // Locators — top navigation
    // -------------------------------------------------------------------------
    private final Locator topNavBar;
    private final Locator userDropdownName;
    private final Locator mainMenuItems;

    // -------------------------------------------------------------------------
    // Locators — Quick Launch widget
    // -------------------------------------------------------------------------
    private final Locator quickLaunchCard;
    private final Locator assignLeaveShortcut;
    private final Locator leaveListShortcut;
    private final Locator timesheetsShortcut;
    private final Locator applyLeaveShortcut;
    private final Locator myLeaveShortcut;
    private final Locator myTimesheetShortcut;

    // -------------------------------------------------------------------------
    // Locators — other widgets
    // -------------------------------------------------------------------------
    private final Locator timeAtWorkCard;
    private final Locator myActionsCard;
    private final Locator todaysLeaveCard;
    private final Locator employeesOnLeaveCard;

    public DashboardPageObjects() {
        super();

        // Header (first h6 on the page is "Dashboard")
        dashboardHeader = page.locator("h6.oxd-text--h6").first();

        // Top navigation
        topNavBar        = page.locator(".oxd-topbar-header");
        userDropdownName = page.locator(".oxd-userdropdown-name");
        mainMenuItems    = page.locator(".oxd-main-menu-item");

        // Quick Launch — entire card + individual shortcut labels
        quickLaunchCard       = page.locator(".oxd-grid-item")
                                    .filter(new Locator.FilterOptions().setHasText("Quick Launch"));
        assignLeaveShortcut   = page.locator("p.oxd-text--p")
                                    .filter(new Locator.FilterOptions().setHasText("Assign Leave"));
        leaveListShortcut     = page.locator("p.oxd-text--p")
                                    .filter(new Locator.FilterOptions().setHasText("Leave List"));
        timesheetsShortcut    = page.locator("p.oxd-text--p")
                                    .filter(new Locator.FilterOptions().setHasText("Timesheets"));
        applyLeaveShortcut    = page.locator("p.oxd-text--p")
                                    .filter(new Locator.FilterOptions().setHasText("Apply Leave"));
        myLeaveShortcut       = page.locator("p.oxd-text--p")
                                    .filter(new Locator.FilterOptions().setHasText("My Leave"));
        myTimesheetShortcut   = page.locator("p.oxd-text--p")
                                    .filter(new Locator.FilterOptions().setHasText("My Timesheet"));

        // Widget cards
        timeAtWorkCard        = page.locator(".oxd-grid-item")
                                    .filter(new Locator.FilterOptions().setHasText("Time at Work"));
        myActionsCard         = page.locator(".oxd-grid-item")
                                    .filter(new Locator.FilterOptions().setHasText("My Actions"));
        todaysLeaveCard       = page.locator(".oxd-grid-item")
                                    .filter(new Locator.FilterOptions().setHasText("Today's Leave"));
        employeesOnLeaveCard  = page.locator(".oxd-grid-item")
                                    .filter(new Locator.FilterOptions().setHasText("Employees on Leave Today"));
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /** Navigates directly to the dashboard URL. */
    public void navigateToDashboard() {
        String dashboardUrl = ConfigReader.getBaseUrl()
                .replace("/auth/login", "/dashboard/index");
        navigateTo(dashboardUrl);
        waitForVisible(dashboardHeader);
    }

    // -------------------------------------------------------------------------
    // Synchronisation
    // -------------------------------------------------------------------------

    /** Waits until the dashboard heading is visible — use after login. */
    public void waitForDashboard() {
        waitForVisible(dashboardHeader);
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    /** Clicks the Assign Leave shortcut in the Quick Launch widget. */
    public void clickAssignLeaveShortcut() {
        waitForVisible(assignLeaveShortcut);
        clickElement(assignLeaveShortcut);
    }

    /** Clicks the Leave List shortcut in the Quick Launch widget. */
    public void clickLeaveListShortcut() {
        waitForVisible(leaveListShortcut);
        clickElement(leaveListShortcut);
    }

    /** Clicks the Timesheets shortcut in the Quick Launch widget. */
    public void clickTimesheetsShortcut() {
        waitForVisible(timesheetsShortcut);
        clickElement(timesheetsShortcut);
    }

    /**
     * Clicks a top navigation menu item by its visible label.
     *
     * @param menuLabel e.g. "Admin", "Leave", "PIM"
     */
    public void clickMainMenuItem(String menuLabel) {
        Locator menuItem = mainMenuItems
                .filter(new Locator.FilterOptions().setHasText(menuLabel));
        waitForVisible(menuItem);
        clickElement(menuItem);
    }

    // -------------------------------------------------------------------------
    // Locator accessors (for assertThat)
    // -------------------------------------------------------------------------

    public Locator getDashboardHeaderLocator()      { return dashboardHeader; }
    public Locator getTopNavBarLocator()            { return topNavBar; }
    public Locator getUserDropdownNameLocator()     { return userDropdownName; }
    public Locator getMainMenuItemsLocator()        { return mainMenuItems; }
    public Locator getQuickLaunchCardLocator()      { return quickLaunchCard; }
    public Locator getAssignLeaveLocator()          { return assignLeaveShortcut; }
    public Locator getLeaveListLocator()            { return leaveListShortcut; }
    public Locator getTimesheetsLocator()           { return timesheetsShortcut; }
    public Locator getApplyLeaveLocator()           { return applyLeaveShortcut; }
    public Locator getMyLeaveLocator()              { return myLeaveShortcut; }
    public Locator getMyTimesheetLocator()          { return myTimesheetShortcut; }
    public Locator getTimeAtWorkCardLocator()       { return timeAtWorkCard; }
    public Locator getMyActionsCardLocator()        { return myActionsCard; }
    public Locator getTodaysLeaveCardLocator()      { return todaysLeaveCard; }
    public Locator getEmployeesOnLeaveCardLocator() { return employeesOnLeaveCard; }

    /**
     * Returns a locator for the Quick Launch shortcut identified by
     * {@code shortcutName}, suitable for {@code assertThat} assertions.
     *
     * @param shortcutName visible label, e.g. "Assign Leave"
     */
    public Locator getQuickLaunchShortcutLocator(String shortcutName) {
        return page.locator("p.oxd-text--p")
                   .filter(new Locator.FilterOptions().setHasText(shortcutName));
    }

    /**
     * Returns a locator for the main-menu item identified by {@code menuLabel}.
     *
     * @param menuLabel visible label, e.g. "Admin"
     */
    public Locator getMainMenuItemLocator(String menuLabel) {
        return mainMenuItems
                .filter(new Locator.FilterOptions().setHasText(menuLabel));
    }

    // -------------------------------------------------------------------------
    // Boolean helpers (kept for backward-compat / non-Playwright assertion use)
    // -------------------------------------------------------------------------

    /** Returns {@code true} if the main dashboard heading is visible. */
    public boolean isDashboardVisible() {
        waitForDashboard();
        return isVisible(dashboardHeader);
    }

    /** Returns the trimmed text of the main dashboard heading. */
    public String getDashboardHeaderText() {
        waitForDashboard();
        return getText(dashboardHeader);
    }

    /** Returns the logged-in user's display name shown in the top navigation. */
    public String getLoggedInUsername() {
        waitForVisible(userDropdownName);
        return getText(userDropdownName);
    }
}
