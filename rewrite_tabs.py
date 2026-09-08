import re

with open("app/src/main/java/com/example/AdminDashboardScreen.kt", "r") as f:
    content = f.read()

# Define the new AdminCatalogTab
admin_catalog_tab = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCatalogTab(viewModel: LoyaltyViewModel) {
    val offers by viewModel.offers.collectAsStateWithLifecycle()
    val dbCategories by viewModel.categories.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("ALL") }
    
    val categories = listOf("ALL") + dbCategories.map { it.name.uppercase() }.distinct()
    
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Product Catalog", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(20.dp))
        
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) com.example.ui.theme.ExtraDarkGreen else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else com.example.ui.theme.BorderSlate),
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val filteredOffers = if (selectedCategory == "ALL") offers else offers.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val chunkedOffers = filteredOffers.chunked(2)
            items(chunkedOffers) { rowOffers ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    for (offer in rowOffers) {
                        Card(
                            modifier = Modifier.weight(1f).height(260.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column {
                                    if (offer.imageUrl != null) {
                                        AsyncImage(
                                            model = offer.imageUrl,
                                            contentDescription = offer.title,
                                            modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.LightGray),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.ImageNotSupported, contentDescription = "No Image", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(offer.title, fontWeight = FontWeight.Bold, maxLines = 2, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(offer.price, color = com.example.ui.theme.MediumGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("/ ea", color = Color.Gray, fontSize = 12.sp)
                                            Surface(color = com.example.ui.theme.BackgroundLight, shape = RoundedCornerShape(4.dp)) {
                                                Text("In Stock", fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.deleteOffer(offer) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.White.copy(alpha=0.7f), androidx.compose.foundation.shape.CircleShape).size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    if (rowOffers.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}"""

# Define the new AdminManageTab
admin_manage_tab = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageTab(viewModel: LoyaltyViewModel) {
    val dbCategories by viewModel.categories.collectAsStateWithLifecycle()
    
    var offerTitle by remember { mutableStateOf("") }
    var offerCategory by remember { mutableStateOf("") }
    var offerPrice by remember { mutableStateOf("") }
    var offerDesc by remember { mutableStateOf("") }
    var isSpecialOffer by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    var newCategoryName by remember { mutableStateOf("") }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    
    var expandedCategory by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        item {
            Text("Manage Categories", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(bottom = 16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newCategoryName, 
                            onValueChange = { newCategoryName = it }, 
                            label = { Text("New Category") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { 
                                if (newCategoryName.isNotBlank()) {
                                    viewModel.addCategory(newCategoryName.uppercase())
                                    newCategoryName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                        ) {
                            Text("Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Existing Categories", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (cat in dbCategories) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp)).padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.name, fontWeight = FontWeight.Bold, color = com.example.ui.theme.DarkGreen)
                                IconButton(onClick = { viewModel.deleteCategory(cat) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                        if (dbCategories.isEmpty()) {
                            Text("No categories yet.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
        item {
            Text("Add New Product", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = com.example.ui.theme.ExtraDarkGreen, modifier = Modifier.padding(bottom = 16.dp))
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderSlate)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(value = offerTitle, onValueChange = { offerTitle = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = offerCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            dbCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        offerCategory = cat.name
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = offerPrice, onValueChange = { offerPrice = it }, label = { Text("Price (e.g. $2.99)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = offerDesc, onValueChange = { offerDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = "Selected Image", modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.LightGray), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imageUri == null) "Select Image" else "Change Image")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isSpecialOffer, onCheckedChange = { isSpecialOffer = it })
                        Text("Mark as Special Bonus Offer")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            val desc = if (isSpecialOffer) "OFFER: $offerDesc" else offerDesc
                            viewModel.addOffer(offerTitle, offerPrice, desc, offerCategory.ifBlank { "General" }, imageUri?.toString())
                            offerTitle = ""
                            offerCategory = ""
                            offerPrice = ""
                            offerDesc = ""
                            imageUri = null
                            isSpecialOffer = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PrimaryGreen)
                    ) {
                        Text("Publish Product")
                    }
                }
            }
        }
    }
}"""

# Find the start and end of AdminCatalogTab
start_idx_catalog = content.find("fun AdminCatalogTab(viewModel: LoyaltyViewModel)")
if start_idx_catalog != -1:
    start_idx_catalog = content.rfind("@OptIn", 0, start_idx_catalog)
    if start_idx_catalog == -1:
        start_idx_catalog = content.rfind("@Composable", 0, content.find("fun AdminCatalogTab(viewModel: LoyaltyViewModel)"))
    
    end_idx_catalog = content.find("@Composable", content.find("fun AdminCatalogTab(viewModel: LoyaltyViewModel)"))
    
    # Replace Catalog
    content = content[:start_idx_catalog] + admin_catalog_tab + "\n\n" + content[end_idx_catalog:]

# Find the start and end of AdminManageTab
start_idx_manage = content.find("fun AdminManageTab(viewModel: LoyaltyViewModel)")
if start_idx_manage != -1:
    start_idx_manage = content.rfind("@Composable", 0, start_idx_manage)
    end_idx_manage = content.find("@Composable", content.find("fun AdminManageTab(viewModel: LoyaltyViewModel)"))
    
    # Replace Manage
    content = content[:start_idx_manage] + admin_manage_tab + "\n\n" + content[end_idx_manage:]

with open("app/src/main/java/com/example/AdminDashboardScreen.kt", "w") as f:
    f.write(content)

