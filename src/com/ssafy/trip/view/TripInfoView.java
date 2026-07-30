package com.ssafy.trip.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.ssafy.trip.model.dto.StoreDto;
import com.ssafy.trip.model.dto.TripDto;
import com.ssafy.trip.model.dto.TripSearchDto;
import com.ssafy.trip.model.service.StoreService;
import com.ssafy.trip.model.service.StoreServiceImpl;
import com.ssafy.trip.model.service.TripService;
import com.ssafy.trip.model.service.TripServiceImpl;

public class TripInfoView {

	/** model들 */
	private TripService tripService;
	private StoreService storeService;

	/** main 화면 */
	private JFrame frame;

	/** 관광지 이미지 표시 Panel */
	private JLabel imgL;
	private JLabel[] tripInfoL;

	/** 조회 조건 */
	private JComboBox<String> findC;
	private JTextField wordTf;
	private JButton searchBt;

	/** 조회 내용 표시할 table */
	private DefaultTableModel tripModel;
	private JTable tripTable;
	private JScrollPane tripPan;
	private String[] title = { "번호", "관광지명", "도로명주소", "지번주소", "전화번호" };

	/** 주변 상권 조회 */
	private JComboBox<Double> radiusC;
	private JComboBox<String> categoryC;
	private JButton storeSearchBt;
	private JLabel storeStatusL;
	private DefaultTableModel storeModel;
	private SwingWorker<List<StoreDto>, Void> storeWorker;
	private String[] storeTitle = { "상호명", "대분류", "세부 업종", "도로명주소", "거리" };

	/** 검색 조건 */
	private String key;
	private String[] choice = { "검색조건선택", "관광지명", "주소" };
	/** 검색할 단어 */
	private String word;

	/** 화면에 표시하고 있는 주택 */
	private TripDto curTrip;

	public TripInfoView() {
		/* Service들 생성 */
		tripService = new TripServiceImpl();
		storeService = new StoreServiceImpl();

		/* 메인 화면 설정 */
		frame = new JFrame("Enjoy! Trip - 즐거운 여행");
//		frame.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e){
//				frame.dispose();
//			}
//		});

		setMain();

		frame.setSize(1200, 800);
		frame.setResizable(true);
		frame.setVisible(true);
		showTripInfo(0);
	}

	private void showTripInfo(int num) {
		curTrip = tripService.search(num);
		if (curTrip == null) {
			return;
		}

		tripInfoL[0].setText("");
		tripInfoL[1].setText("");
		tripInfoL[2].setText(curTrip.getTouristDestination());
		tripInfoL[3].setText(curTrip.getStreetAddress());
		tripInfoL[4].setText(curTrip.getLotAddress());
		tripInfoL[5].setText(curTrip.getLat() + "");
		tripInfoL[6].setText(curTrip.getLng() + "");
		tripInfoL[7].setText(curTrip.getTel());
		tripInfoL[8].setText(curTrip.getInfo());
		tripInfoL[9].setText("");

		ImageIcon icon = null;
		if (curTrip.getImg() != null && curTrip.getImg().trim().length() != 0) {
			String img = curTrip.getImg();
			File file = new File("img", img);

			if (!file.exists())
				img = "no_image.jpg";
			icon = new ImageIcon("img/" + img);

		} else {
			icon = new ImageIcon("img/no_image.jpg");
		}
		Image image = icon.getImage();
		Image changeImage = image.getScaledInstance(570, 470, Image.SCALE_SMOOTH);
		ImageIcon changeIcon = new ImageIcon(changeImage);
		imgL.setIcon(changeIcon);

		searchNearbyStores();
	}

	/** 메인 화면인 관광지 목록을 위한 화면 셋팅하는 메서드 */
	public void setMain() {

		/* 왼쪽 화면을 위한 설정 */
		JPanel left = new JPanel(new BorderLayout());
		JPanel leftCenter = new JPanel(new BorderLayout(0, 10));
		JPanel leftR = new JPanel(new GridLayout(10, 2));
		leftR.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		String[] info = { "", "", "관광지명", "도로명주소", "지번주소", "위도", "경도", "전화번호", "관광지정보", "" };
		int size = info.length;
		JLabel infoL[] = new JLabel[size];
		tripInfoL = new JLabel[size];
		for (int i = 0; i < size; i++) {
			infoL[i] = new JLabel(info[i]);
			tripInfoL[i] = new JLabel("");
			leftR.add(infoL[i]);
			leftR.add(tripInfoL[i]);
		}
		imgL = new JLabel();
		leftCenter.add(imgL, "Center");
		leftCenter.add(leftR, "South");

		left.add(new JLabel("관광지 정보", JLabel.CENTER), "North");
		left.add(leftCenter, "Center");

		/* 오른쪽 화면을 위한 설정 */
		JPanel right = new JPanel(new BorderLayout());
		JPanel rightTop = new JPanel(new GridLayout(4, 2));

		JPanel rightTop2 = new JPanel(new GridLayout(1, 3));
		String[] item = { "검색조건선택", "관광지명", "주소" };
		findC = new JComboBox<String>(item);
		wordTf = new JTextField();
		searchBt = new JButton("검색");

		rightTop2.add(findC);
		rightTop2.add(wordTf);
		rightTop2.add(searchBt);

		rightTop.add(new Label(""));
		rightTop.add(new Label(""));
		rightTop.add(rightTop2);
		rightTop.add(new Label(""));

		JPanel tripListPanel = new JPanel(new BorderLayout());
		tripModel = new DefaultTableModel(title, 20);
		tripTable = new JTable(tripModel);
		tripPan = new JScrollPane(tripTable);
		tripTable.setColumnSelectionAllowed(true);
		tripListPanel.add(new JLabel("관광지 정보", JLabel.CENTER), "North");
		tripListPanel.add(tripPan, "Center");

		JPanel storePanel = createStorePanel();
		JTabbedPane resultTabs = new JTabbedPane();
		resultTabs.addTab("관광지 목록", tripListPanel);
		resultTabs.addTab("주변 상권", storePanel);

		right.add(rightTop, "North");
		right.add(resultTabs, "Center");

		JPanel mainP = new JPanel(new GridLayout(1, 2));

		mainP.add(left);
		mainP.add(right);

		mainP.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		frame.add(mainP, "Center");

		tripTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = tripTable.getSelectedRow();
				int code = Integer.parseInt(((String) tripModel.getValueAt(row, 0)).trim());
				showTripInfo(code);
			}
		});

		// complete code #01
		// 아래의 코드를 참조하여 아래 라인을 uncomment 하고 searchBt.addActionList() 를 Lambda 표현식으로 바꾸세요.
		// searchBt.addActionListener( /* 여기 */ );

		// 참조코드 시작 - 위 코드를 완성 후 삭제 또는 comment 처리하세요.
//		ActionListener buttonHandler = new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				searchTrips();
//			}
//		};
		
		searchBt.addActionListener(e -> searchTrips());
		// 참조코드 종료

		showTrips();
	}

	private JPanel createStorePanel() {
		JPanel storePanel = new JPanel(new BorderLayout(0, 5));
		JPanel storeFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		radiusC = new JComboBox<Double>(new Double[] { 0.5, 1.0, 3.0, 5.0 });
		radiusC.setSelectedItem(1.0);
		categoryC = new JComboBox<String>(new String[] {
				"전체", "음식", "숙박", "소매", "여가·오락", "교육",
				"보건의료", "수리·개인", "시설관리·임대", "과학·기술"
		});
		storeSearchBt = new JButton("주변 검색");
		storeSearchBt.addActionListener(e -> searchNearbyStores());

		storeFilterPanel.add(new JLabel("반경(km)"));
		storeFilterPanel.add(radiusC);
		storeFilterPanel.add(new JLabel("업종"));
		storeFilterPanel.add(categoryC);
		storeFilterPanel.add(storeSearchBt);

		storeModel = new DefaultTableModel(storeTitle, 0);
		JTable storeTable = new JTable(storeModel);
		storeTable.setAutoCreateRowSorter(true);
		storeTable.getColumnModel().getColumn(0).setPreferredWidth(120);
		storeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
		storeTable.getColumnModel().getColumn(3).setPreferredWidth(260);
		storeTable.getColumnModel().getColumn(4).setPreferredWidth(60);

		storeStatusL = new JLabel("관광지를 선택하면 주변 상권을 검색합니다.");
		storeStatusL.setBorder(BorderFactory.createEmptyBorder(3, 8, 5, 8));

		storePanel.add(storeFilterPanel, BorderLayout.NORTH);
		storePanel.add(new JScrollPane(storeTable), BorderLayout.CENTER);
		storePanel.add(storeStatusL, BorderLayout.SOUTH);
		return storePanel;
	}

	private void searchNearbyStores() {
		if (curTrip == null || radiusC == null || categoryC == null) {
			return;
		}
		if (storeWorker != null && !storeWorker.isDone()) {
			storeWorker.cancel(true);
		}

		final TripDto selectedTrip = curTrip;
		final double radiusKm = (Double) radiusC.getSelectedItem();
		final String category = (String) categoryC.getSelectedItem();

		storeSearchBt.setEnabled(false);
		storeStatusL.setText(selectedTrip.getTouristDestination() + " 주변 상권을 검색 중입니다...");
		storeModel.setRowCount(0);

		storeWorker = new SwingWorker<List<StoreDto>, Void>() {
			@Override
			protected List<StoreDto> doInBackground() throws Exception {
				return storeService.searchNearby(selectedTrip, radiusKm, category, 100);
			}

			@Override
			protected void done() {
				if (storeWorker != this) {
					return;
				}
				storeSearchBt.setEnabled(true);
				try {
					List<StoreDto> stores = get();
					showStores(stores);
					storeStatusL.setText(String.format(
							"%s 반경 %.1fkm · %s · %d개",
							selectedTrip.getTouristDestination(), radiusKm, category, stores.size()));
				} catch (CancellationException e) {
					storeStatusL.setText("주변 상권 검색이 취소되었습니다.");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					storeStatusL.setText("주변 상권 검색이 중단되었습니다.");
				} catch (ExecutionException e) {
					String message = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
					storeStatusL.setText("검색 실패: " + message);
				}
			}
		};
		storeWorker.execute();
	}

	private void showStores(List<StoreDto> stores) {
		storeModel.setRowCount(0);
		for (StoreDto store : stores) {
			storeModel.addRow(new Object[] {
					getDisplayStoreName(store),
					store.getCategoryLarge(),
					store.getCategorySmall(),
					store.getAddress(),
					formatDistance(store.getDistance())
			});
		}
	}

	private String getDisplayStoreName(StoreDto store) {
		if (store.getBranchName() == null || store.getBranchName().isEmpty()) {
			return store.getStoreName();
		}
		return store.getStoreName() + " " + store.getBranchName();
	}

	private String formatDistance(double distanceKm) {
		if (distanceKm < 1.0) {
			return Math.round(distanceKm * 1000) + "m";
		}
		return String.format("%.1fkm", distanceKm);
	}

	/** 검색 조건에 맞는 관광지 검색 */
	private void searchTrips() {
		word = wordTf.getText().trim();
		key = choice[findC.getSelectedIndex()];
		showTrips();
	}

	/**
	 * 관광지 목록을 갱신하기 위한 메서드
	 */
	public void showTrips() {
		TripSearchDto tripSearchDto = new TripSearchDto();
		if (key != null) {
			if (key.equals("관광지명")) {
				tripSearchDto.setTouristDestination(word);
			} else if (key.equals("주소")) {
				tripSearchDto.setSido(word);
			}
		}

		if (word == null || word.trim().length() == 0)
			findC.setSelectedIndex(0);

		List<TripDto> trips = tripService.searchAll(tripSearchDto);
		if (trips != null) {
			int i = 0;
			String[][] data = new String[trips.size()][5];
			for (TripDto trip : trips) {
				data[i][0] = "" + trip.getNum();
				data[i][1] = trip.getTouristDestination();
				data[i][2] = trip.getStreetAddress();
				data[i][3] = trip.getLotAddress();
				data[i++][4] = trip.getTel();
			}
			tripModel.setDataVector(data, title);
		}
	}

//	public static void main(String[] args) {
//		new TripInfoView();
//	}
}
