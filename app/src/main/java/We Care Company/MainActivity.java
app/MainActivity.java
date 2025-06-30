<!DOCTYPE html>
<html lang="my">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xiang Yuan Hotel - Management Dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Myanmar:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #3498db;
            --secondary: #2c3e50;
            --success: #27ae60;
            --warning: #f39c12;
            --danger: #e74c3c;
            --light: #f8f9fa;
            --dark: #343a40;
            --gray: #6c757d;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Noto Sans Myanmar', sans-serif;
        }

        body {
            background-color: #f5f7fa;
            color: #333;
            line-height: 1.6;
        }

        .app-container {
            display: flex;
            min-height: 100vh;
        }

        /* Sidebar Styles */
        .sidebar {
            width: 250px;
            background: var(--secondary);
            color: white;
            padding: 20px 0;
            transition: all 0.3s ease;
            position: fixed;
            height: 100vh;
            overflow-y: auto;
            z-index: 1000;
        }

        .logo-section {
            padding: 0 20px 20px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            margin-bottom: 20px;
        }

        .logo-section h3 {
            font-weight: 700;
            margin-bottom: 0;
        }

        .logo-section h3 span {
            color: var(--primary);
        }

        .nav-menu {
            list-style: none;
            padding: 0 15px;
        }

        .nav-item {
            margin-bottom: 5px;
        }

        .nav-link {
            display: flex;
            align-items: center;
            color: rgba(255,255,255,0.8);
            text-decoration: none;
            padding: 12px 15px;
            border-radius: 5px;
            transition: all 0.3s;
        }

        .nav-link:hover, .nav-link.active {
            background: rgba(255,255,255,0.1);
            color: white;
        }

        .nav-link i {
            margin-right: 12px;
            width: 20px;
            text-align: center;
        }

        /* Main Content Styles */
        .main-content {
            flex: 1;
            margin-left: 250px;
            padding: 20px;
            transition: all 0.3s;
        }

        /* Header */
        .header {
            background: white;
            border-radius: 12px;
            padding: 15px 20px;
            margin-bottom: 25px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .page-title {
            display: flex;
            align-items: center;
            margin-bottom: 0;
        }

        .page-title i {
            margin-right: 10px;
            color: var(--primary);
        }

        .user-info {
            display: flex;
            align-items: center;
        }

        .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 12px;
            border: 2px solid var(--primary);
        }

        .user-details h6 {
            margin-bottom: 0;
        }

        .user-details small {
            color: var(--gray);
        }

        /* Stats Cards */
        .stats-row {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 15px;
            margin-bottom: 30px;
        }

        .stat-card {
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
            transition: transform 0.3s, box-shadow 0.3s;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.12);
        }

        .stat-content {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 25px 15px;
            color: white;
            text-align: center;
        }

        .stat-icon {
            font-size: 36px;
            margin-bottom: 15px;
        }

        .stat-number {
            font-size: 28px;
            font-weight: 700;
            margin: 5px 0;
        }

        /* Room Cards */
        .section-header {
            display: flex;
            align-items: center;
            margin: 30px 0 20px;
            color: var(--primary);
            font-weight: 700;
            font-size: 18px;
        }

        .section-header i {
            margin-right: 10px;
        }

        .rooms-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }

        .room-card {
            border-radius: 12px;
            overflow: hidden;
            background: white;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            transition: all 0.3s;
        }

        .room-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.12);
        }

        .room-image {
            height: 180px;
            background-size: cover;
            background-position: center;
            position: relative;
        }

        .room-type-badge {
            position: absolute;
            top: 12px;
            right: 12px;
            background: rgba(0,0,0,0.7);
            color: white;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .room-details {
            padding: 18px;
        }

        .room-number {
            font-size: 18px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .room-price {
            font-weight: 600;
            margin-top: 8px;
        }

        .room-status {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 600;
            margin-top: 10px;
        }

        .status-available {
            background-color: #d4edda;
            color: #155724;
        }

        .status-occupied {
            background-color: #f8d7da;
            color: #721c24;
        }

        .status-reserved {
            background-color: #fff3cd;
            color: #856404;
        }

        .status-maintenance {
            background-color: #d1ecf1;
            color: #0c5460;
        }

        /* Recent Bookings */
        .bookings-container {
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 30px;
        }

        .booking-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .booking-table {
            width: 100%;
            border-collapse: collapse;
        }

        .booking-table th {
            background: var(--primary);
            color: white;
            text-align: left;
            padding: 12px 15px;
            font-weight: 600;
        }

        .booking-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #eee;
        }

        .booking-table tr:hover {
            background-color: rgba(52, 152, 219, 0.05);
        }

        .status-badge {
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .badge-confirmed {
            background-color: #d4edda;
            color: #155724;
        }

        .badge-pending {
            background-color: #fff3cd;
            color: #856404;
        }

        .badge-new {
            background-color: #cce5ff;
            color: #004085;
        }

        .badge-suspended {
            background-color: #f8d7da;
            color: #721c24;
        }

        /* Quick Actions */
        .actions-container {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }

        .action-buttons {
            display: grid;
            grid-template-columns: 1fr;
            gap: 12px;
            margin-bottom: 25px;
        }

        .action-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 14px;
            border-radius: 8px;
            border: none;
            color: white;
            font-weight: 600;
            text-align: center;
            transition: all 0.3s;
        }

        .action-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }

        .action-btn i {
            margin-right: 8px;
            font-size: 18px;
        }

        .room-types-list {
            list-style: none;
            padding: 0;
        }

        .room-type-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }

        .room-type-count {
            background: var(--primary);
            color: white;
            padding: 3px 10px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 600;
        }

        /* Footer */
        .footer {
            text-align: center;
            padding: 20px;
            color: var(--gray);
            font-size: 14px;
            margin-top: 30px;
            border-top: 1px solid #eee;
        }

        /* Mobile Menu Button */
        .mobile-menu-btn {
            display: none;
            background: var(--primary);
            color: white;
            border: none;
            border-radius: 5px;
            padding: 8px 12px;
            font-size: 18px;
        }

        /* Responsive Design */
        @media (max-width: 1200px) {
            .stats-row, .rooms-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 992px) {
            .sidebar {
                transform: translateX(-100%);
                width: 280px;
            }

            .sidebar.active {
                transform: translateX(0);
            }

            .main-content {
                margin-left: 0;
            }

            .mobile-menu-btn {
                display: block;
            }
        }

        @media (max-width: 768px) {
            .stats-row, .rooms-grid {
                grid-template-columns: 1fr;
            }

            .user-details {
                display: none;
            }

            .booking-table {
                display: block;
                overflow-x: auto;
            }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <div class="sidebar">
            <div class="logo-section">
                <h3>Xiang Yuan <span>Hotel</span></h3>
                <p class="text-muted mb-0">စီမံခန့်ခွဲမှုစနစ်</p>
            </div>

            <ul class="nav-menu">
                <li class="nav-item">
                    <a href="#" class="nav-link active">
                        <i class="fas fa-tachometer-alt"></i> ဒက်ရှ်ဘုတ်
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-bed"></i> အခန်းများ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-users"></i> ဖောက်သည်များ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-calendar-check"></i> ကြိုတင်မှာယူမှုများ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-money-bill-wave"></i> ငွေစာရင်းများ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-chart-bar"></i> အစီရင်ခံစာများ
                    </a>
                </li>
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="fas fa-cog"></i> ဆက်တင်များ
                    </a>
                </li>
                <li class="nav-item mt-4">
                    <a href="#" class="nav-link">
                        <i class="fas fa-sign-out-alt"></i> ထွက်မည်
                    </a>
                </li>
            </ul>
        </div>

        <!-- Main Content -->
        <div class="main-content">
            <!-- Header -->
            <div class="header">
                <div>
                    <button class="mobile-menu-btn">
                        <i class="fas fa-bars"></i>
                    </button>
                    <h4 class="page-title d-inline-block ms-3 mb-0">
                        <i class="fas fa-tachometer-alt"></i> ဒက်ရှ်ဘုတ်
                    </h4>
                </div>
                <div class="user-info">
                    <img src="https://randomuser.me/api/portraits/men/41.jpg" alt="User" class="user-avatar">
                    <div class="user-details">
                        <h6 class="mb-0">ဦးအောင်မင်း</h6>
                        <small class="text-muted">မန်နေဂျာ</small>
                    </div>
                </div>
            </div>

            <!-- Stats Cards -->
            <div class="stats-row">
                <div class="stat-card" style="background: var(--primary);">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <i class="fas fa-bed"></i>
                        </div>
                        <h5>စုစုပေါင်းအခန်း</h5>
                        <div class="stat-number">45</div>
                    </div>
                </div>

                <div class="stat-card" style="background: var(--success);">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <i class="fas fa-user-check"></i>
                        </div>
                        <h5>လက်ရှိဖောက်သည်</h5>
                        <div class="stat-number">32</div>
                    </div>
                </div>

                <div class="stat-card" style="background: var(--warning);">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <i class="fas fa-calendar-alt"></i>
                        </div>
                        <h5>ယနေ့ကြိုတင်မှာယူမှု</h5>
                        <div class="stat-number">8</div>
                    </div>
                </div>

                <div class="stat-card" style="background: var(--danger);">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <i class="fas fa-door-open"></i>
                        </div>
                        <h5>လွတ်နေသောအခန်း</h5>
                        <div class="stat-number">7</div>
                    </div>
                </div>
            </div>

            <!-- Room Status Section -->
            <div class="section-header">
                <i class="fas fa-bed"></i>
                <span>အခန်းအခြေအနေများ</span>
            </div>

            <div class="rooms-grid">
                <div class="room-card">
                    <div class="room-image" style="background-image: url('https://images.unsplash.com/photo-1611892440504-42a792e24d32?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80');">
                        <span class="room-type-badge">ဒီလပ်စ်</span>
                    </div>
                    <div class="room-details">
                        <div class="room-number">အခန်း ၁၀၁</div>
                        <div>အခန်းအမျိုးအစား - ဒီလပ်စ်</div>
                        <div class="room-price">ဈေးနှုန်း - $80/ည</div>
                        <span class="room-status status-available">လွတ်နေသည်</span>
                    </div>
                </div>

                <div class="room-card">
                    <div class="room-image" style="background-image: url('https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80');">
                        <span class="room-type-badge">စံခန်း</span>
                    </div>
                    <div class="room-details">
                        <div class="room-number">အခန်း ၂၀၂</div>
                        <div>အခန်းအမျိုးအစား - စံခန်း</div>
                        <div class="room-price">ဈေးနှုန်း - $120/ည</div>
                        <span class="room-status status-occupied">အသုံးပြုနေဆဲ</span>
                    </div>
                </div>

                <div class="room-card">
                    <div class="room-image" style="background-image: url('https://images.unsplash.com/photo-1618773928121-c32242e63f39?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80');">
                        <span class="room-type-badge">ဒီလပ်စ် စူပါ</span>
                    </div>
                    <div class="room-details">
                        <div class="room-number">အခန်း ၃၀၁</div>
                        <div>အခန်းအမျိုးအစား - ဒီလပ်စ် စူပါ</div>
                        <div class="room-price">ဈေးနှုန်း - $100/ည</div>
                        <span class="room-status status-reserved">ကြိုတင်မှာယူထား</span>
                    </div>
                </div>

                <div class="room-card">
                    <div class="room-image" style="background-image: url('https://images.unsplash.com/photo-1590490360182-c33d57733427?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80');">
                        <span class="room-type-badge">သီးသန့်ခန်း</span>
                    </div>
                    <div class="room-details">
                        <div class="room-number">အခန်း ၄၀၁</div>
                        <div>အခန်းအမျိုးအစား - သီးသန့်ခန်း</div>
                        <div class="room-price">ဈေးနှုန်း - $150/ည</div>
                        <span class="room-status status-maintenance">ပြုပြင်နေဆဲ</span>
                    </div>
                </div>
            </div>

            <!-- Recent Bookings and Quick Actions -->
            <div class="row">
                <div class="col-md-8">
                    <div class="bookings-container">
                        <div class="booking-header">
                            <div class="section-header">
                                <i class="fas fa-calendar-check"></i>
                                <span>လတ်တလော ကြိုတင်မှာယူမှုများ</span>
                            </div>
                        </div>

                        <div class="table-responsive">
                            <table class="booking-table">
                                <thead>
                                    <tr>
                                        <th>ဖောက်သည်အမည်</th>
                                        <th>အခန်း</th>
                                        <th>အမျိုးအစား</th>
                                        <th>စတင်မည့်ရက်</th>
                                        <th>ပြီးဆုံးမည့်ရက်</th>
                                        <th>အခြေအနေ</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>ဦးမြင့်ကြည်</td>
                                        <td>၂၀၁</td>
                                        <td>စံခန်း</td>
                                        <td>၂၀၂၃-၁၀-၁၅</td>
                                        <td>၂၀၂၃-၁၀-၁၇</td>
                                        <td><span class="status-badge badge-confirmed">အတည်ပြုပြီး</span></td>
                                    </tr>
                                    <tr>
                                        <td>ဒေါ်ခင်ခင်ဝေ</td>
                                        <td>၃၀၂</td>
                                        <td>ဒီလပ်စ် စူပါ</td>
                                        <td>၂၀၂၃-၁၀-၁၆</td>
                                        <td>၂၀၂၃-၁၀-၁၉</td>
                                        <td><span class="status-badge badge-suspended">ဆိုင်းငံ့ထား</span></td>
                                    </tr>
                                    <tr>
                                        <td>ဦးအောင်မြင့်ဦး</td>
                                        <td>၁၀၂</td>
                                        <td>ဒီလပ်စ်</td>
                                        <td>၂၀၂၃-၁၀-၁၈</td>
                                        <td>၂၀၂၃-၁၀-၂၀</td>
                                        <td><span class="status-badge badge-new">ကြိုတင်မှာယူထား</span></td>
                                    </tr>
                                    <tr>
                                        <td>ဒေါ်နီလာထွေး</td>
                                        <td>၄၀၂</td>
                                        <td>သီးသန့်ခန်း</td>
                                        <td>၂၀၂၃-၁၀-၂၀</td>
                                        <td>၂၀၂၃-၁၀-၂၅</td>
                                        <td><span class="status-badge badge-new">အသစ်</span></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="actions-container">
                        <div class="section-header">
                            <i class="fas fa-bolt"></i>
                            <span>လုပ်ဆောင်ချက်များ</span>
                        </div>

                        <div class="action-buttons">
                            <button class="action-btn" style="background: var(--primary);">
                                <i class="fas fa-plus-circle"></i> အခန်းအသစ်ထည့်ရန်
                            </button>
                            <button class="action-btn" style="background: var(--success);">
                                <i class="fas fa-user-plus"></i> ဖောက်သည်အသစ်ထည့်ရန်
                            </button>
                            <button class="action-btn" style="background: var(--warning);">
                                <i class="fas fa-calendar-plus"></i> ကြိုတင်မှာယူမှုအသစ်
                            </button>
                            <button class="action-btn" style="background: #9b59b6;">
                                <i class="fas fa-file-invoice-dollar"></i> ငွေစာရင်းထုတ်ရန်
                            </button>
                        </div>

                        <div class="section-header mt-4">
                            <i class="fas fa-list"></i>
                            <span>အခန်းအမျိုးအစားများ</span>
                        </div>

                        <ul class="room-types-list">
                            <li class="room-type-item">
                                <span>ဒီလပ်စ်</span>
                                <span class="room-type-count">15</span>
                            </li>
                            <li class="room-type-item">
                                <span>ဒီလပ်စ် စူပါ</span>
                                <span class="room-type-count">10</span>
                            </li>
                            <li class="room-type-item">
                                <span>စံခန်း</span>
                                <span class="room-type-count">12</span>
                            </li>
                            <li class="room-type-item">
                                <span>သီးသန့်ခန်း</span>
                                <span class="room-type-count">8</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <!-- Footer -->
            <div class="footer">
                <p>© 2023 Xiang Yuan Hotel - စီမံခန့်ခွဲမှုစနစ် | အားလုံးသော ဥပဒေအခွင့်အရေးများ ကာကွယ်ထားပါသည်။</p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Toggle mobile menu
        document.querySelector('.mobile-menu-btn').addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('active');
        });

        // Update current date
        const today = new Date();
        const options = { year: 'numeric', month: 'long', day: 'numeric' };
        document.getElementById('current-date').textContent = today.toLocaleDateString('my-MM', options);
    </script>
</body>
</html>