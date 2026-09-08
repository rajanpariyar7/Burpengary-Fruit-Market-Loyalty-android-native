#!/bin/bash
sed -i 's/fun addOffer(title: String, price: String, desc: String)/fun addOffer(title: String, price: String, desc: String, category: String = "General", imageUrl: String? = null)/g' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
sed -i 's/Offer(title = title, price = price, description = desc)/Offer(title = title, price = price, description = desc, category = category, imageUrl = imageUrl)/g' app/src/main/java/com/example/ui/viewmodel/LoyaltyViewModel.kt
