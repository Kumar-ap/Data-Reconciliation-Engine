package com.data.recon.engine.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class FileService {
	
	
	public Map<String, List<String>> matchFile(File fileX, File fileY) {
		
		List<Transaction> transactionsX = new ArrayList<Transaction>();
		List<Transaction> transactionsY = new ArrayList<Transaction>();
		List<String> exactMatch = new ArrayList<String>();
		List<String> xBreak = new ArrayList<String>();
		List<String> yBreak = new ArrayList<String>();
		List<String> weakMatch = new ArrayList<String>();
		try (Stream<String> streamX = Files.lines(Paths.get(fileX.getAbsolutePath()));
				Stream<String> streamY = Files.lines(Paths.get(fileY.getAbsolutePath()))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			streamX.forEach(str->{
				String[] transactionX = str.split(";");

				Date date =null;
				try {
					date = simpleDateFormat.parse(transactionX[2].trim());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				transactionsX.add(new Transaction(transactionX[0].trim(),transactionX[1].trim(),date,new BigDecimal(transactionX[3].trim())));
			});

			streamY.forEach(str->{
				String[] transactionY = str.split(";");
				Date date = null;
				try {
					date = simpleDateFormat.parse(transactionY[2].trim());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				transactionsY.add(new Transaction(transactionY[0].trim(),transactionY[1].trim(),date,new BigDecimal(transactionY[3].trim())));
			});
			System.out.println(transactionsX.size());
			for(int i = 0,j=0; i < transactionsX.size()&&j<transactionsY.size();i++,j++) {
				Transaction transactionX =transactionsX.get(i);
				Transaction transactionY =transactionsY.get(j);
				//System.out.println(transactionX+"\n"+transactionY);
				if(transactionX.equals(transactionY)) {
					exactMatch.add(transactionX.getTransactionID()+""+transactionY.getTransactionID());
				}else {
					System.out.println("X-"+i+"-"+transactionX.getAccountID());
					System.out.println("Y-"+j+"-"+transactionY.getAccountID());

					if(!transactionX.getAccountID().equals(transactionY.getAccountID())) {
						xBreak.add(transactionX.getTransactionID());
						yBreak.add(transactionY.getTransactionID());
					}else {
						System.out.println("------------");
						BigDecimal amountX =transactionX.getAmount();
						BigDecimal amountY =transactionY.getAmount();
						
						System.out.println(amountX+"---"+amountY);
						if(amountX.compareTo(amountY)>=1) {
							System.out.println("equal="+(amountX==amountY)+" "+((double)amountX.subtract(amountY).doubleValue())+"-"+amountX.compareTo(amountY));
							if(amountX.subtract(amountY).doubleValue() <= 0.01) {
								Date dateX = transactionX.getPostingDate();
								Date dateY = transactionY.getPostingDate();
								System.out.println(dateX.compareTo(dateY));
								if(dateX.compareTo(dateY)==0) {
									weakMatch.add(transactionX.getTransactionID()+transactionY.getTransactionID());
								}else {
									System.out.println("Compare="+dateX.compareTo(dateY));
									if(dateX.compareTo(dateY) <= 3 || dateX.compareTo(dateY) >=-3) {
										Calendar calendar = Calendar.getInstance();
										calendar.setTime(dateX);
										int dayOfWeekX = calendar.get(Calendar.DAY_OF_WEEK);
										calendar.setTime(dateY);
										int dayOfWeekY = calendar.get(Calendar.DAY_OF_WEEK);
										if((dayOfWeekX == 6 && (dayOfWeekY==7 ||dayOfWeekY==0 ||dayOfWeekY==1))
												||(dayOfWeekY == 6 && (dayOfWeekX==7 ||dayOfWeekX==0 ||dayOfWeekX==1))
												|| (dayOfWeekX-dayOfWeekY <= 1 && dayOfWeekX-dayOfWeekY >= -1)) {
											weakMatch.add(transactionX.getTransactionID()+transactionY.getTransactionID());
										}else {
											xBreak.add(transactionX.getTransactionID());
											yBreak.add(transactionY.getTransactionID());
										}
									}else {
										xBreak.add(transactionX.getTransactionID());
										yBreak.add(transactionY.getTransactionID());
									}
								}

							}else {
								xBreak.add(transactionX.getTransactionID());
								yBreak.add(transactionY.getTransactionID());
							}

						}else {
							BigDecimal temp = amountX;
							amountX = amountY;
							amountY = temp;
							if(amountX.compareTo(amountY)>=0) {
								if(amountX.subtract(amountY).doubleValue()>0.01) {
									xBreak.add(transactionX.getTransactionID());
									yBreak.add(transactionY.getTransactionID());
								}else {
									System.out.println("equal="+(amountX==amountY)+" "+((double)amountX.subtract(amountY).doubleValue())+"-"+amountX.compareTo(amountY));
									Date dateX = transactionX.getPostingDate();
									Date dateY = transactionY.getPostingDate();
									if(dateX.compareTo(dateY)==0) {
										weakMatch.add(transactionX.getTransactionID()+transactionY.getTransactionID());
									}else {
										System.out.println("Compare="+dateX.compareTo(dateY));
										if(dateX.compareTo(dateY) <= 3 || dateX.compareTo(dateY) >=-3) {
											Calendar calendar = Calendar.getInstance();
											calendar.setTime(dateX);
											int dayOfWeekX = calendar.get(Calendar.DAY_OF_WEEK);
											calendar.setTime(dateY);
											int dayOfWeekY = calendar.get(Calendar.DAY_OF_WEEK);
											if((dayOfWeekX == 6 && (dayOfWeekY==7 ||dayOfWeekY==1 ||dayOfWeekY==2))
													||(dayOfWeekY == 6 && (dayOfWeekX==7 ||dayOfWeekX==1 ||dayOfWeekX==2))
													|| (dayOfWeekX-dayOfWeekY <= 1 && dayOfWeekX-dayOfWeekY >= -1)) {
												weakMatch.add(transactionX.getTransactionID()+transactionY.getTransactionID());
											}else {
												xBreak.add(transactionX.getTransactionID());
												yBreak.add(transactionY.getTransactionID());
											}
										}else {
											xBreak.add(transactionX.getTransactionID());
											yBreak.add(transactionY.getTransactionID());
										}

									}
								}
							}else {
								xBreak.add(transactionX.getTransactionID());
								yBreak.add(transactionY.getTransactionID());
							}

						}
					}


				}


			}


		} catch (IOException e) {
			e.printStackTrace();
			
		}
		System.out.println("ExactMatch");
		exactMatch.forEach(System.out::println);
		System.out.println("Weak Match");
		weakMatch.forEach(System.out::println);
		System.out.println("X Break");
		xBreak.forEach(System.out::println);
		System.out.println("Y Break");
		yBreak.forEach(System.out::println);
		
		Map<String, List<String>> mapData= new HashMap<>();
		mapData.put("Exact Match",exactMatch);
		mapData.put("Weak Match",weakMatch);
		mapData.put("X Break",xBreak);
		mapData.put("Y Break",yBreak);
		return mapData;

	}
		
}
	        
