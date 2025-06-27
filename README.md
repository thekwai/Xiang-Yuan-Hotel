from kivymd.app import MDApp
from kivy.lang import Builder
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.core.window import Window
from kivymd.uix.list import TwoLineListItem, ThreeLineListItem
from kivymd.uix.button import MDRaisedButton
from kivymd.uix.dialog import MDDialog
import random

Window.size = (360, 640)  # Mobile screen size

# Main backend class (unchanged)
class SuperAppMyanmar:
    def __init__(self):
        self.users = {}
        self.drivers = {}
        self.merchants = {
            1: {"name": "BBQ Restaurant", "type": "food", "menu": {"Chicken Rice": 5000, "Salad": 3000}},
            2: {"name": "24/7 Mart", "type": "grocery", "items": {"Milk": 2500, "Eggs (10)": 2000}}
        }
        self.orders = {}
        self.rides = {}
        self.order_counter = 1
        self.ride_counter = 1

    def register_user(self, user_id, name, phone, address):
        self.users[user_id] = {
            "name": name,
            "phone": phone,
            "address": address,
            "wallet_balance": 0
        }

    def add_money_to_wallet(self, user_id, amount):
        if user_id in self.users:
            self.users[user_id]["wallet_balance"] += amount
            return f"Wallet updated. New balance: {self.users[user_id]['wallet_balance']} MMK"
        return "User not found!"

    def request_ride(self, user_id, vehicle_type, pickup, destination):
        self.rides[self.ride_counter] = {
            "user_id": user_id,
            "driver_id": f"DRV{random.randint(100,999)}",
            "vehicle_type": vehicle_type,
            "pickup": pickup,
            "destination": destination,
            "status": "Driver Assigned",
            "fare": 3000
        }
        self.ride_counter += 1
        return self.ride_counter-1

    def place_order(self, user_id, merchant_id, items):
        total = 0
        merchant = self.merchants[merchant_id]
        for item, qty in items.items():
            if merchant["type"] == "food":
                total += merchant["menu"].get(item, 0) * qty
            elif merchant["type"] == "grocery":
                total += merchant["items"].get(item, 0) * qty
        
        self.orders[self.order_counter] = {
            "user_id": user_id,
            "merchant_id": merchant_id,
            "items": items,
            "total": total,
            "status": "Preparing",
            "driver_id": f"DRV{random.randint(100,999)}"
        }
        self.order_counter += 1
        return self.order_counter-1

    def make_payment(self, user_id, order_id=None, ride_id=None, amount=None):
        if order_id:
            amount = self.orders[order_id]["total"]
        elif ride_id:
            amount = self.rides[ride_id]["fare"]
        
        if self.users[user_id]["wallet_balance"] >= amount:
            self.users[user_id]["wallet_balance"] -= amount
            return True, f"Payment successful! Remaining balance: {self.users[user_id]['wallet_balance']} MMK"
        return False, "Insufficient balance!"

    def track_order(self, order_id):
        return self.orders.get(order_id, {}).get("status", "Invalid ID")
    
    def track_ride(self, ride_id):
        return self.rides.get(ride_id, {}).get("status", "Invalid ID")

# KivyMD Frontend
class LoginScreen(Screen):
    def verify_user(self):
        user_id = self.ids.user_id.text
        app = MDApp.get_running_app()
        if user_id in app.backend.users:
            app.current_user = user_id
            app.root.current = "dashboard"
        else:
            self.ids.error_label.text = "User not registered!"

class DashboardScreen(Screen):
    pass

class RideScreen(Screen):
    def request_ride(self):
        app = MDApp.get_running_app()
        ride_id = app.backend.request_ride(
            app.current_user,
            self.ids.vehicle_type.text,
            self.ids.pickup.text,
            self.ids.destination.text
        )
        self.ids.ride_status.text = f"Ride #{ride_id} requested!\nDriver assigned"
        self.ids.pay_btn.disabled = False

    def pay_ride(self):
        app = MDApp.get_running_app()
        success, msg = app.backend.make_payment(app.current_user, ride_id=self.ids.ride_id.text)
        MDDialog(title="Payment", text=msg).open()

class FoodScreen(Screen):
    def __init__(self, **kw):
        super().__init__(**kw)
        self.selected_items = {}
        self.populate_menu()

    def populate_menu(self):
        app = MDApp.get_running_app()
        merchant = app.backend.merchants[1]
        container = self.ids.food_container
        container.clear_widgets()
        
        for item, price in merchant["menu"].items():
            btn = MDRaisedButton(
                text=f"{item} - {price} MMK",
                size_hint_y=None,
                height=60,
                on_release=lambda x, i=item: self.select_item(i)
            )
            container.add_widget(btn)

    def select_item(self, item):
        if item not in self.selected_items:
            self.selected_items[item] = 1
        else:
            self.selected_items[item] += 1
        self.update_selection()

    def update_selection(self):
        self.ids.selected_items.text = "\n".join(
            [f"{item} x {qty}" for item, qty in self.selected_items.items()]
        )

    def place_order(self):
        if not self.selected_items: return
        app = MDApp.get_running_app()
        order_id = app.backend.place_order(app.current_user, 1, self.selected_items)
        self.ids.order_status.text = f"Order #{order_id} placed!"
        self.ids.pay_btn.disabled = False
        self.selected_items = {}

class GroceryScreen(Screen):
    # Similar implementation to FoodScreen
    pass

class WalletScreen(Screen):
    def update_balance(self):
        app = MDApp.get_running_app()
        user = app.backend.users.get(app.current_user, {})
        self.ids.balance_label.text = f"Current Balance: {user.get('wallet_balance', 0)} MMK"

    def add_money(self):
        try:
            amount = int(self.ids.topup_amount.text)
            app = MDApp.get_running_app()
            result = app.backend.add_money_to_wallet(app.current_user, amount)
            self.update_balance()
            MDDialog(title="Wallet", text=result).open()
        except ValueError:
            MDDialog(title="Error", text="Invalid amount").open()

class TrackScreen(Screen):
    def check_status(self):
        track_id = self.ids.track_id.text
        app = MDApp.get_running_app()
        
        if track_id.startswith("R"):
            status = app.backend.track_ride(int(track_id[1:]))
            self.ids.status_label.text = f"Ride Status: {status}"
        else:
            status = app.backend.track_order(int(track_id[1:]))
            self.ids.status_label.text = f"Order Status: {status}"

class MainApp(MDApp):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.backend = SuperAppMyanmar()
        self.current_user = None
        # Pre-register a sample user
        self.backend.register_user("user1", "Aung Aung", "09123456789", "Yangon")
        self.backend.add_money_to_wallet("user1", 50000)
        
    def build(self):
        self.theme_cls.primary_palette = "Teal"
        self.theme_cls.theme_style = "Light"
        
        sm = ScreenManager()
        sm.add_widget(LoginScreen(name="login"))
        sm.add_widget(DashboardScreen(name="dashboard"))
        sm.add_widget(RideScreen(name="ride"))
        sm.add_widget(FoodScreen(name="food"))
        sm.add_widget(GroceryScreen(name="grocery"))
        sm.add_widget(WalletScreen(name="wallet"))
        sm.add_widget(TrackScreen(name="track"))
        return sm

# Kivy UI String
Builder.load_string('''
<LoginScreen>:
    BoxLayout:
        orientation: 'vertical'
        padding: dp(40)
        spacing: dp(20)
        
        MDLabel:
            text: "SuperApp Myanmar"
            font_style: "H4"
            halign: "center"
            size_hint_y: None
            height: self.texture_size[1]
            
        MDTextField:
            id: user_id
            hint_text: "Enter User ID"
            icon_left: "account"
            
        MDLabel:
            id: error_label
            text: ""
            theme_text_color: "Error"
            
        MDRaisedButton:
            text: "LOGIN"
            on_release: root.verify_user()
            pos_hint: {"center_x": 0.5}
            
        Widget:

<DashboardScreen>:
    BoxLayout:
        orientation: 'vertical'
        
        MDToolbar:
            title: "SuperApp Dashboard"
            left_action_items: [["logout", lambda x: app.root.current = "login"]]
            elevation: 10
            
        ScrollView:
            GridLayout:
                cols: 2
                size_hint_y: None
                height: self.minimum_height
                padding: dp(20)
                spacing: dp(20)
                
                MDRaisedButton:
                    text: "Ride Hailing"
                    on_release: app.root.current = "ride"
                    size_hint: None, None
                    size: dp(150), dp(150)
                    
                MDRaisedButton:
                    text: "Food Delivery"
                    on_release: app.root.current = "food"
                    size_hint: None, None
                    size: dp(150), dp(150)
                    
                MDRaisedButton:
                    text: "Quick Commerce"
                    on_release: app.root.current = "grocery"
                    size_hint: None, None
                    size: dp(150), dp(150)
                    
                MDRaisedButton:
                    text: "My Wallet"
                    on_release: app.root.current = "wallet"
                    size_hint: None, None
                    size: dp(150), dp(150)
                    
                MDRaisedButton:
                    text: "Track Orders"
                    on_release: app.root.current = "track"
                    size_hint: None, None
                    size: dp(150), dp(150)

<RideScreen>:
    BoxLayout:
        orientation: 'vertical'
        
        MDToolbar:
            title: "Ride Hailing"
            left_action_items: [["arrow-left", lambda x: app.root.current = "dashboard"]]
            
        ScrollView:
            BoxLayout:
                orientation: 'vertical'
                size_hint_y: None
                height: dp(500)
                padding: dp(20)
                spacing: dp(15)
                
                MDLabel:
                    text: "Request a Ride"
                    font_style: "H6"
                    
                MDTextField:
                    id: pickup
                    hint_text: "Pickup Location"
                    
                MDTextField:
                    id: destination
                    hint_text: "Destination"
                    
                MDTextField:
                    id: vehicle_type
                    hint_text: "Vehicle Type (Bike/Car)"
                    
                MDRaisedButton:
                    text: "Request Ride"
                    on_release: root.request_ride()
                    
                MDLabel:
                    id: ride_status
                    text: ""
                    markup: True
                    
                MDTextField:
                    id: ride_id
                    hint_text: "Ride ID for Payment"
                    
                MDRaisedButton:
                    id: pay_btn
                    text: "Pay with Wallet"
                    disabled: True
                    on_release: root.pay_ride()

<FoodScreen>:
    BoxLayout:
        orientation: 'vertical'
        
        MDToolbar:
            title: "Food Delivery"
            left_action_items: [["arrow-left", lambda x: app.root.current = "dashboard"]]
            
        ScrollView:
            BoxLayout:
                orientation: 'vertical'
                size_hint_y: None
                height: dp(800)
                padding: dp(20)
                spacing: dp(15)
                
                MDLabel:
                    text: "BBQ Restaurant Menu"
                    font_style: "H6"
                    
                BoxLayout:
                    id: food_container
                    orientation: 'vertical'
                    size_hint_y: None
                    height: dp(300)
                    
                MDLabel:
                    text: "Selected Items:"
                    font_style: "Subtitle1"
                    
                MDLabel:
                    id: selected_items
                    text: ""
                    
                MDRaisedButton:
                    text: "Place Order"
                    on_release: root.place_order()
                    
                MDLabel:
                    id: order_status
                    text: ""
                    
                MDRaisedButton:
                    id: pay_btn
                    text: "Pay Now"
                    disabled: True
                    on_release: 
                        app.backend.make_payment(app.current_user, order_id=root.ids.order_status.text.split("#")[1][0])
                        app.root.current = "dashboard"

# Additional screens would follow similar patterns
''')

if __name__ == "__main__":
    MainApp().run() 
