package in.krharsh17.programmersdate;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface Constants {

    int mapDefaultHeight = 14;
    int runtime = 15000;
    int totalCheckPoints = 50;
    int upperMatchPoints = 10;
    int lowerMatchPoints = 5;
    int poseTotalChecks = 480;
    int poseUpperMatchCriteria = 250;
    int poseLowerMatchCriteria = 150;
    String TAG = "DEFAULT";

    DatabaseReference couplesRef = FirebaseDatabase.getInstance().getReference().child("couples");
    DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("tasks");

    String sharedPrefName = "PDate";

    LatLng sacBuilding = new LatLng(25.6193583, 85.1720750);
    LatLng cafeteria = new LatLng(25.6191857, 85.1720269);
    LatLng mainBuilding = new LatLng(25.6207500, 85.1719484);
    LatLng civilDept = new LatLng(25.6212939, 85.1722132);
    LatLng computerCentre = new LatLng(25.6213556, 85.1727258);
    LatLng tennisCourt = new LatLng(25.6209605, 85.1726816);
    LatLng directorBungalow = new LatLng(25.6213756, 85.1732707);
    LatLng guestHouse = new LatLng(25.6210010, 85.1728988);
    LatLng mechanicalWorkshop = new LatLng(25.6206688, 85.1730678);
    LatLng gangaHostel = new LatLng(25.6210509, 85.1740468);
    LatLng cseDept = new LatLng(25.6205914, 85.1742185);
    LatLng kosiHostel = new LatLng(25.6206004, 85.1745043);
    LatLng CWRS = new LatLng(25.6194965, 85.1739721);
    LatLng library = new LatLng(25.6205615, 85.1715333);
    LatLng canteenGopalJi = new LatLng(25.6204324, 85.1716841);
    LatLng canteenShuklaJi = new LatLng(25.6210593, 85.1717103);
    LatLng electricalDept = new LatLng(25.6210615, 85.1706243);
    LatLng mechanicalDept = new LatLng(25.6212746, 85.1706508);
    LatLng newElectricalDept = new LatLng(25.6206310, 85.1710297);
    LatLng electronicsDept = new LatLng(25.6204114, 85.1736354);
    LatLng physicsDept = new LatLng(25.6204414, 85.1734081);
    LatLng ground = new LatLng(25.6200502, 85.1726082);
    LatLng soneAHostel = new LatLng(25.6181565, 85.1719772);
    LatLng soneBHostel = new LatLng(25.6185166, 85.1719802);
    LatLng miniAuditorium = new LatLng(25.6198062, 85.1737271);

    String sacBuildingText = "SAC";
    String cafeteriaText = "Cafeteria";
    String mainBuildingText = "Main Building";
    String civilDeptText = "Civil Department";
    String computerCentreText = "Computer Centre";
    String tennisCourtText = "Tennis Court";
    String directorBungalowText = "Director's Bungalow";
    String guestHouseText = "Guest House";
    String mechanicalWorkshopText = "Mechanical Workshop";
    String gangaHostelText = "Ganga Hostel";
    String cseDeptText = "CSE Department";
    String kosiHostelText = "Kosi Hostel";
    String CWRSText = "CWRS";
    String libraryText = "Library";
    String canteenGopalJiText = "Canteen Gopal Ji";
    String canteenShuklaJiText = "Canteen Shukla Ji";
    String electricalDeptText = "Electrical Department";
    String mechanicalDeptText = "Mechanical Department";
    String newElectricalDeptText = "New Electrical Department";
    String electronicsDeptText = "Electronics Department";
    String physicsDeptText = "Physics Department";
    String groundText = "Main Ground";
    String soneAHostelText = "Sone A Hostel";
    String soneBHostelText = "Sone B Hostel";
    String miniAuditoriumText = "Mini Auditorium";


    LatLngBounds NITP_BOUNDS = new LatLngBounds(new LatLng(25.619097, 85.170036),
            new LatLng(25.621264, 85.175347));


    String helpTextPose = "Strike the given pose together with your partner and get it captured to ace this round!";
    String helpTextQR = "Find a hidden QR code in the marked areas!";
    String helpTextBar = "Find a hidden bar code in the show areas!";
    String helpTextLogo = "Find a popular logo in the set areas!";
    String helpTextTwister = "Say the given tongue twister clearly";

    String taskTypeLogo = "LOGO";
    String taskTypeQR = "QR";
    String taskTypeBar = "BAR";
    String taskTypePose = "POSE";
    String taskTypeTwister = "TWISTER";

    int MODEL_WIDTH = 257;
    int MODEL_HEIGHT = 257;

    String TAG = "DEBUG";
    int numLevels = 10;
    int numLocs = 1;

    long timeLimit = 1000 * 60 * 60 * 3;

    int REQUEST_CAMERA_PERMISSION = 1;

}
