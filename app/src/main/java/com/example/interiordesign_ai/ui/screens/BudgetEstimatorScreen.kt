package com.example.interiordesign_ai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interiordesign_ai.viewmodel.BudgetBreakdown
import com.example.interiordesign_ai.viewmodel.BudgetViewModel

// ─── State / City / District Data ────────────────────────────────────────────

private val indianStates = listOf(
    "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
    "Delhi NCR", "Goa", "Gujarat", "Haryana", "Himachal Pradesh",
    "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Ladakh",
    "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
    "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim",
    "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
    "West Bengal", "Chandigarh", "Puducherry"
)

private val stateToCity = mapOf(
    "Andhra Pradesh"       to listOf("Visakhapatnam", "Vijayawada", "Guntur", "Kurnool", "Tirupati", "Nellore"),
    "Arunachal Pradesh"    to listOf("Itanagar", "Naharlagun", "Pasighat", "Tezpur"),
    "Assam"                to listOf("Guwahati", "Silchar", "Dibrugarh", "Jorhat", "Tezpur", "Nagaon"),
    "Bihar"                to listOf("Patna", "Gaya", "Muzaffarpur", "Bhagalpur", "Darbhanga", "Bodh Gaya"),
    "Chhattisgarh"         to listOf("Raipur", "Bhilai", "Bilaspur", "Korba", "Durg", "Rajnandgaon"),
    "Delhi NCR"            to listOf("New Delhi", "Noida", "Gurgaon", "Faridabad", "Ghaziabad", "Greater Noida"),
    "Goa"                  to listOf("Panaji", "Margao", "Vasco da Gama", "Mapusa", "Ponda"),
    "Gujarat"              to listOf("Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar", "Gandhinagar"),
    "Haryana"              to listOf("Gurugram", "Faridabad", "Panipat", "Ambala", "Karnal", "Hisar"),
    "Himachal Pradesh"     to listOf("Shimla", "Manali", "Dharamshala", "Solan", "Mandi", "Kullu"),
    "Jammu and Kashmir"    to listOf("Srinagar", "Jammu", "Kathua", "Udhampur", "Punch", "Anantnag"),
    "Jharkhand"            to listOf("Ranchi", "Jamshedpur", "Dhanbad", "Bokaro", "Hazaribagh", "Deoghar"),
    "Karnataka"            to listOf("Bengaluru", "Mysuru", "Mangaluru", "Hubli", "Belagavi", "Kalaburagi"),
    "Kerala"               to listOf("Thiruvananthapuram", "Kochi", "Kozhikode", "Thrissur", "Kollam", "Kannur"),
    "Ladakh"               to listOf("Leh", "Kargil"),
    "Madhya Pradesh"       to listOf("Bhopal", "Indore", "Jabalpur", "Gwalior", "Ujjain", "Sagar"),
    "Maharashtra"          to listOf("Mumbai", "Pune", "Nagpur", "Nashik", "Aurangabad", "Thane"),
    "Manipur"              to listOf("Imphal", "Thoubal", "Bishnupur", "Churachandpur"),
    "Meghalaya"            to listOf("Shillong", "Tura", "Jowai", "Nongpoh"),
    "Mizoram"              to listOf("Aizawl", "Lunglei", "Champhai", "Serchhip"),
    "Nagaland"             to listOf("Kohima", "Dimapur", "Mokokchung", "Wokha"),
    "Odisha"               to listOf("Bhubaneswar", "Cuttack", "Rourkela", "Berhampur", "Sambalpur", "Puri"),
    "Punjab"               to listOf("Ludhiana", "Amritsar", "Jalandhar", "Patiala", "Bathinda", "Mohali"),
    "Rajasthan"            to listOf("Jaipur", "Jodhpur", "Udaipur", "Kota", "Ajmer", "Bikaner"),
    "Sikkim"               to listOf("Gangtok", "Namchi", "Gyalshing", "Mangan"),
    "Tamil Nadu"           to listOf("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli"),
    "Telangana"            to listOf("Hyderabad", "Warangal", "Karimnagar", "Nizamabad", "Khammam", "Secunderabad"),
    "Tripura"              to listOf("Agartala", "Dharmanagar", "Udaipur", "Kailashahar"),
    "Uttar Pradesh"        to listOf("Lucknow", "Kanpur", "Agra", "Varanasi", "Prayagraj", "Noida"),
    "Uttarakhand"          to listOf("Dehradun", "Haridwar", "Rishikesh", "Nainital", "Haldwani", "Roorkee"),
    "West Bengal"          to listOf("Kolkata", "Howrah", "Durgapur", "Asansol", "Siliguri", "Bardhaman"),
    "Chandigarh"           to listOf("Chandigarh"),
    "Puducherry"           to listOf("Puducherry", "Karaikal", "Mahe", "Yanam")
)

private val cityToAreas = mapOf(
    // Andhra Pradesh
    "Visakhapatnam" to listOf("MVP Colony", "Gajuwaka", "Madhurawada", "Rushikonda", "Seethammadhara", "Dwaraka Nagar"),
    "Vijayawada"    to listOf("Benz Circle", "Governorpet", "Labbipet", "Moghalrajpuram", "Patamata", "Suryaraopeta"),
    "Guntur"        to listOf("Brodipet", "Kothapet", "Arundelpet", "Nagarampalem", "Pattabhipuram"),
    "Kurnool"       to listOf("C Camp", "Budhawarpet", "Madhavaram", "Venkataramana Colony", "Bellary Road"),
    "Tirupati"      to listOf("Alipiri", "Renigunta", "Korlagunta", "Srinivasa Nagar"),
    "Nellore"       to listOf("Magunta Layout", "Santhapet", "Pogathota", "Grand Trunk Road"),
    // Assam
    "Guwahati"      to listOf("Dispur", "Paltan Bazaar", "Lakhimpur", "Kahilipara", "Beltola", "Jalukbari"),
    "Silchar"       to listOf("Rangirkhari", "Gandhi Nagar", "Ambikapatty", "Tarapur"),
    "Dibrugarh"     to listOf("A.T. Road", "Pranabnagar", "Charing Cross", "Lahoal"),
    // Bihar
    "Patna"         to listOf("Boring Road", "Patna Sahib", "Danapur", "Kankarbagh", "Rajendra Nagar", "Bailey Road"),
    "Gaya"          to listOf("Bodh Gaya Road", "Station Road", "Akharaghat"),
    // Chhattisgarh
    "Raipur"        to listOf("Shankar Nagar", "Pandri", "Civil Lines", "Tatibandh", "Telibandha"),
    // Delhi NCR
    "New Delhi"     to listOf("Connaught Place", "Karol Bagh", "Lajpat Nagar", "Saket", "Dwarka", "Rohini"),
    "Noida"         to listOf("Sector 18", "Sector 62", "Sector 137", "Greater Noida West", "Sector 50"),
    "Gurgaon"       to listOf("DLF Phase 1", "DLF Phase 3", "Sushant Lok", "Golf Course Road", "MG Road"),
    "Faridabad"     to listOf("Sector 21C", "NIT Faridabad", "Ballabhgarh", "Sector 82"),
    "Ghaziabad"     to listOf("Indirapuram", "Vasundhara", "Raj Nagar", "Kaushambi", "Vaishali"),
    "Greater Noida" to listOf("Alpha", "Beta", "Gamma", "Delta", "Zeta", "Knowledge Park"),
    // Goa
    "Panaji"        to listOf("Fontainhas", "Miramar", "Dona Paula", "Ribandar"),
    "Margao"        to listOf("Monte Hill", "Borda", "Fatorda", "Aquem"),
    // Gujarat
    "Ahmedabad"     to listOf("Navrangpura", "Satellite", "Prahlad Nagar", "Maninagar", "Bopal", "Vastrapur"),
    "Surat"         to listOf("Adajan", "Vesu", "Pal", "Athwa Lines", "Katargam", "Rander"),
    "Vadodara"      to listOf("Alkapuri", "Akota", "Manjalpur", "Fatehgunj", "Waghodia"),
    "Rajkot"        to listOf("Kalawad Road", "University Road", "Mavdi", "Yagnik Road"),
    // Haryana
    "Gurugram"      to listOf("DLF City", "Sector 56", "Palam Vihar", "Sohna Road", "Cyber City"),
    // Himachal Pradesh
    "Shimla"        to listOf("The Mall", "Lakkar Bazaar", "Chhota Shimla", "Boileauganj"),
    // JK
    "Srinagar"      to listOf("Lal Chowk", "Rajbagh", "Jawahar Nagar", "Dalgate", "Hyderpora"),
    "Jammu"         to listOf("Gandhi Nagar", "Bakshi Nagar", "Talab Tillo", "Sainik Colony"),
    // Jharkhand
    "Ranchi"        to listOf("Harmu", "Lalpur", "Bariatu", "Morabadi", "Kanke"),
    "Jamshedpur"    to listOf("Bistupur", "Sakchi", "Kadma", "Telco Colony"),
    // Karnataka
    "Bengaluru"     to listOf("Koramangala", "Indiranagar", "Whitefield", "Jayanagar", "Hebbal", "HSR Layout"),
    "Mysuru"        to listOf("Vijayanagar", "Kuvempunagar", "Gokulam", "Hebbal", "Saraswathipuram"),
    "Mangaluru"     to listOf("Hampankatta", "Kadri", "Bejai", "Ullal", "Kankanady"),
    // Kerala
    "Thiruvananthapuram" to listOf("Pattom", "Kowdiar", "Vazhuthacaud", "Kazhakuttam", "Kesavadasapuram"),
    "Kochi"         to listOf("Ernakulam", "Fort Kochi", "Edapally", "Kakkanad", "Aluva", "Thrikkakara"),
    "Kozhikode"     to listOf("Calicut Beach", "Palayam", "Mavoor Road", "Chevayur"),
    // MP
    "Bhopal"        to listOf("Arera Colony", "MP Nagar", "Kolar Road", "Shahpura", "Berasia Road"),
    "Indore"        to listOf("Vijay Nagar", "LIG Colony", "Palasia", "Bhawarkua", "Rajwada"),
    // Maharashtra
    "Mumbai"        to listOf("Andheri", "Bandra", "Powai", "Navi Mumbai", "Thane", "Dadar"),
    "Pune"          to listOf("Kothrud", "Baner", "Wakad", "Hadapsar", "Hinjewadi", "Viman Nagar"),
    "Nagpur"        to listOf("Dharampeth", "Sitabuldi", "Laxmi Nagar", "Civil Lines", "Sadar"),
    "Nashik"        to listOf("College Road", "Dwarka", "Indira Nagar", "Pethroad"),
    "Aurangabad"    to listOf("Cidco", "Garkheda", "Cantonment", "N-2 Hudco"),
    "Thane"         to listOf("Majiwada", "Manpada", "Kopri", "Cadbury Junction"),
    // Odisha
    "Bhubaneswar"   to listOf("Saheed Nagar", "Nayapalli", "Patia", "Khandagiri", "Chandrasekharpur"),
    "Cuttack"       to listOf("Badambadi", "Buxi Bazaar", "Chaudhury Bazaar", "College Square"),
    // Punjab
    "Ludhiana"      to listOf("Model Town", "Sarabha Nagar", "BRS Nagar", "Gurdev Nagar"),
    "Amritsar"      to listOf("Lawrence Road", "Ranjit Avenue", "Green Avenue", "Majitha Road"),
    "Jalandhar"     to listOf("Model Town", "Guru Nanak Pura", "Basti Sheikh", "Lajpat Nagar"),
    // Rajasthan
    "Jaipur"        to listOf("Vaishali Nagar", "Malviya Nagar", "C Scheme", "Mansarovar", "Jagatpura"),
    "Jodhpur"       to listOf("Ratanada", "Shastri Nagar", "Paota", "Sojati Gate"),
    "Udaipur"       to listOf("Sukhadia Circle", "Fatehpura", "Bhuwana", "Hiran Magri"),
    // Tamil Nadu
    "Chennai"       to listOf("Anna Nagar", "Velachery", "Adyar", "Porur", "Nungambakkam", "OMR"),
    "Coimbatore"    to listOf("RS Puram", "Gandhipuram", "Saibaba Colony", "Peelamedu", "Vadavalli"),
    "Madurai"       to listOf("Anna Nagar", "KK Nagar", "Thirunagar", "Avaniyapuram"),
    "Tiruchirappalli" to listOf("Srirangam", "Ariyamangalam", "Palpannai", "Woraiyur"),
    "Salem"         to listOf("Fairlands", "Five Roads", "Gugai", "Suramangalam"),
    "Tirunelveli"   to listOf("Palayamkottai", "Melapalayam", "Vannarpettai"),
    // Telangana
    "Hyderabad"     to listOf("Banjara Hills", "Jubilee Hills", "Gachibowli", "Hitech City", "Madhapur", "Kondapur"),
    "Warangal"      to listOf("Hanamkonda", "Kazipet", "Desaipet", "Nakkalagutta"),
    "Secunderabad"  to listOf("Trimulgherry", "West Marredpally", "SP Road", "Marredpally"),
    // UP
    "Lucknow"       to listOf("Hazratganj", "Gomtinagar", "Aliganj", "Indira Nagar", "Vibhuti Khand"),
    "Kanpur"        to listOf("Civil Lines", "Swaroop Nagar", "Kidwai Nagar", "Arya Nagar"),
    "Agra"          to listOf("Taj Nagri", "Kamla Nagar", "Shahganj", "Dayal Bagh"),
    "Varanasi"      to listOf("Lanka", "Sigra", "Nadesar", "Cantonment"),
    "Prayagraj"     to listOf("Civil Lines", "George Town", "Naini", "Phaphamau"),
    // Uttarakhand
    "Dehradun"      to listOf("Rajpur Road", "Clement Town", "Prem Nagar", "Vasant Vihar", "GMS Road"),
    "Haridwar"      to listOf("Jwalapur", "Bhadrabad", "Shivalik Nagar", "Ranipur"),
    // West Bengal
    "Kolkata"       to listOf("Salt Lake", "New Town", "Park Street", "Howrah", "Kasba", "Behala"),
    "Howrah"        to listOf("Shibpur", "Bally", "Dasnagar", "Golabari"),
    "Durgapur"      to listOf("City Centre", "Bidhannagar", "Benipur", "Nachan Road"),
    "Siliguri"      to listOf("Pradhan Nagar", "Hakimpara", "Sevoke Road", "Bidhan Road"),
    // Chandigarh
    "Chandigarh"    to listOf("Sector 17", "Sector 22", "Sector 35", "Sector 44", "Mohali"),
    // Puducherry
    "Puducherry"    to listOf("White Town", "Ariyankuppam", "Villianur", "Oulgaret")
)

// Default fallback areas
private val defaultAreas = listOf("Area 1", "Area 2", "Area 3", "Old Town", "New Town", "Main Road")

private val roomTypes = listOf("Living Room", "Bedroom", "Kitchen", "Bathroom", "Office", "Dining Room", "Kids Room")
private val qualityTiers = listOf("Low", "Medium", "Premium", "Luxury")

// ─── BudgetEstimatorScreen ────────────────────────────────────────────────────

@Composable
fun BudgetEstimatorScreen(
    onBack: () -> Unit = {},
    onHomeTab: () -> Unit = {},
    onExploreTab: () -> Unit = {},
    onAccountTab: () -> Unit = {},
    budgetViewModel: BudgetViewModel = viewModel()
) {
    var selectedState    by remember { mutableStateOf("") }
    var selectedCity     by remember { mutableStateOf("") }
    var selectedArea     by remember { mutableStateOf("") }
    var selectedRoomType by remember { mutableStateOf("Living Room") }
    var carpetArea       by remember { mutableStateOf("") }
    var selectedTier     by remember { mutableStateOf("Medium") }

    val breakdown by budgetViewModel.breakdown.collectAsState()

    val allFieldsFilled = selectedState.isNotEmpty()
            && selectedCity.isNotEmpty()
            && selectedArea.isNotEmpty()
            && carpetArea.isNotEmpty()

    val cities    = stateToCity[selectedState] ?: emptyList()
    val areas     = cityToAreas[selectedCity]  ?: defaultAreas

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            BudgetBottomNav(selectedTab = 2, onHomeTab = onHomeTab, onExploreTab = onExploreTab, onAccountTab = onAccountTab)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            Text(
                text = "Budget Estimator",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ── Location Details Card ────────────────────────────────────────
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = AppPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Location Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A2E)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // State dropdown
                DropdownField(
                    label = "State",
                    selected = selectedState.ifEmpty { "Select State" },
                    options = indianStates,
                    enabled = true,
                    onSelect = {
                        selectedState = it
                        selectedCity = ""
                        selectedArea = ""
                        budgetViewModel.clearResult()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // City dropdown (enabled only after state selected)
                DropdownField(
                    label = "City",
                    selected = selectedCity.ifEmpty { "Select City" },
                    options = cities,
                    enabled = selectedState.isNotEmpty(),
                    onSelect = {
                        selectedCity = it
                        selectedArea = ""
                        budgetViewModel.clearResult()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // District / Area dropdown (enabled only after city selected)
                DropdownField(
                    label = "District / Area",
                    selected = selectedArea.ifEmpty { "Select Area" },
                    options = areas,
                    enabled = selectedCity.isNotEmpty(),
                    onSelect = {
                        selectedArea = it
                        budgetViewModel.clearResult()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Room Specs Card ──────────────────────────────────────────────
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.SpaceDashboard,
                        contentDescription = null,
                        tint = AppPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Room Specs",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A2E)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Room type dropdown
                DropdownField(
                    label = "Room Type",
                    selected = selectedRoomType,
                    options = roomTypes,
                    enabled = true,
                    onSelect = {
                        selectedRoomType = it
                        budgetViewModel.clearResult()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Carpet area
                Text(
                    text = "Carpet Area (sq. ft)",
                    fontSize = 13.sp,
                    color = Color(0xFF555555)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = carpetArea,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() } && it.length <= 6) {
                            carpetArea = it
                            budgetViewModel.clearResult()
                        }
                    },
                    placeholder = { Text("e.g. 250", color = Color(0xFFBBBBBB)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor   = Color(0xFFF5F5F5),
                        unfocusedBorderColor    = Color.Transparent,
                        focusedBorderColor      = AppPurple
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Quality Tier
                Text(
                    text = "Quality Tier",
                    fontSize = 13.sp,
                    color = Color(0xFF555555)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    qualityTiers.forEach { tier ->
                        val isSelected = tier == selectedTier
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    color = if (isSelected) AppPurple else Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedTier = tier
                                    budgetViewModel.clearResult()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tier,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF666666),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Calculate Button ──────────────────────────────────────────────
            Button(
                onClick = {
                    if (allFieldsFilled) {
                        budgetViewModel.calculateEstimate(
                            state       = selectedState,
                            city        = selectedCity,
                            district    = selectedArea,
                            roomType    = selectedRoomType,
                            areaSqft    = carpetArea.toDoubleOrNull() ?: 0.0,
                            qualityTier = selectedTier
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = allFieldsFilled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (breakdown != null) Color(0xFF888888) else AppPurple,
                    disabledContainerColor = Color(0xFFCCCCCC)
                )
            ) {
                Text(
                    text = if (breakdown != null) "Recalculate Estimate" else "Calculate Estimate",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Result Card ───────────────────────────────────────────────────
            AnimatedVisibility(
                visible = breakdown != null,
                enter = fadeIn() + expandVertically(),
                exit  = fadeOut()
            ) {
                breakdown?.let { bd ->
                    ResultCard(breakdown = bd, city = selectedCity)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Dropdown Field ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = label,
        fontSize = 13.sp,
        color = Color(0xFF555555)
    )
    Spacer(modifier = Modifier.height(6.dp))

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (enabled) Color(0xFFF5F5F5) else Color(0xFFEEEEEE),
                    shape = RoundedCornerShape(10.dp)
                )
                .menuAnchor()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected,
                    fontSize = 15.sp,
                    color = if (selected.startsWith("Select") || !enabled)
                        Color(0xFFAAAAAA) else Color(0xFF1A1A2E),
                    fontWeight = if (!selected.startsWith("Select") && enabled)
                        FontWeight.Medium else FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (enabled) Color(0xFF888888) else Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            color = if (option == selected) AppPurple else Color(0xFF1A1A2E),
                            fontWeight = if (option == selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (option == selected) AppPurple.copy(alpha = 0.08f) else Color.White
                    )
                )
            }
        }
    }
}

// ─── Result Card ─────────────────────────────────────────────────────────────

@Composable
private fun ResultCard(breakdown: BudgetBreakdown, city: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Estimated Cost",
                    fontSize = 14.sp,
                    color = AppPurple,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = city,
                    fontSize = 13.sp,
                    color = AppPurple,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = breakdown.formattedTotal,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            CostRow("Flooring",    breakdown.flooring)
            CostRow("Walls",       breakdown.walls)
            CostRow("Ceiling",     breakdown.ceiling)
            CostRow("Furniture",   breakdown.furniture)
            CostRow("Lighting",    breakdown.lighting)
            CostRow("Accessories", breakdown.accessories)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "*Estimates based on real 2025-26 market rates for $city",
                fontSize = 11.sp,
                color = Color(0xFFAAAAAA)
            )
        }
    }
}

@Composable
private fun CostRow(label: String, value: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF555555))
        Text(
            text = when {
                value >= 100_000 -> "₹%.1fL".format(value / 100_000.0)
                value >= 1_000   -> "₹%.1fk".format(value / 1_000.0)
                else             -> "₹$value"
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A2E)
        )
    }
}

// ─── Section Card Wrapper ─────────────────────────────────────────────────────

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

// ─── Budget Bottom Nav ────────────────────────────────────────────────────────

@Composable
private fun BudgetBottomNav(
    selectedTab: Int,
    onHomeTab: () -> Unit,
    onExploreTab: () -> Unit = {},
    onAccountTab: () -> Unit = {}
) {
    val items = listOf(
        Pair("Home",    Icons.Filled.Home),
        Pair("Explore", Icons.Filled.GridView),
        Pair("Budget",  Icons.Filled.CurrencyRupee),
        Pair("Account", Icons.Filled.Person)
    )
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (label, icon) ->
                val isSelected = index == selectedTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            when (index) {
                                0 -> onHomeTab()
                                1 -> onExploreTab()
                                3 -> onAccountTab()
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) AppPurple else Color(0xFFAAAAAA),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) AppPurple else Color(0xFFAAAAAA)
                    )
                }
            }
        }
    }
}
