import java.util.*;
import java.io.*;
public class Main {
    static int[][] map;
    static List<Army> armys;
    static int[][] armyMap;
    static StringBuilder sb = new StringBuilder();
    static int n;
    static int[] dx = new int[]{-1,1,0,0};  // 상하좌우
    static int[] dy = new int[]{0,0,-1,1};
    static int[][][] medusaRoute;   // 메두사 최단거리
    static int[][] rocks;


    public static class Army{
        int x;
        int y;
        boolean isRock;
        public Army(int x, int y, boolean isRock){
            this.x = x;
            this.y = y;
            this.isRock = isRock;
        }

    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        armyMap = new int[n][n];
        
        st = new StringTokenizer(br.readLine());
        int sx = Integer.parseInt(st.nextToken());
        int sy = Integer.parseInt(st.nextToken());
        int ex = Integer.parseInt(st.nextToken());
        int ey = Integer.parseInt(st.nextToken());

        armys = new ArrayList<>();
        st = new StringTokenizer(br.readLine());
        for(int i=0;i<m;i++){
            // 전사
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            armys.add(new Army(x, y, false));
            armyMap[x][y]++;
        }
        // 맵 도로 0
        map = new int[n][n];
        for(int i=0;i<n;i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0;j<n;j++){
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 메두사가 공원까지 갈 수 없다면 -1출력 (메두사 최단거리 루트 확보)
        if(!isAbleDestination(sx,sy,ex,ey)) {
            sb.append("-1");
        }else{
            solution(sx,sy,ex,ey);
        }

        System.out.println(sb);
    }
    public static void solution(int sx, int sy,int ex, int ey){
        int x = sx;
        int y = sy;


        while(true){
            int p1=0;
            int p2=0;
            int p3=0;
            // 1. 메두사 물리 이동, 전사 만나면 제거
            int[] cur = movingMedusa(x,y);  // 공원도착 종료
            x = cur[0];
            y = cur[1];
            if(x == ex && y == ey){
                sb.append("0");
                break;
            }
            // 2. 메두사의 시선
            p2 += medusaGaze(x,y);

            // 3. 전사들의 이동
            int[] scores = movingArmys(x,y);
            p1 += scores[0];
            p3 += scores[1];

            sb.append(p1+" "+p2+" "+p3);
            sb.append('\n');

        }

    }
    public static int[] move(int x, int y, int start, int medusaX, int medusaY){
        int dist = Math.abs(medusaX - x) + Math.abs(medusaY - y);
        int xx = x;
        int yy = y;
        for(int d=start;d<4 + start;d++){
            int dd = d % 4;
            int nx = x + dx[dd];
            int ny = y + dy[dd];
            if(nx<0||ny<0||nx>=n||ny>=n||rocks[nx][ny] == 1)continue;

            int next = Math.abs(medusaX - nx) + Math.abs(medusaY - ny);
            if(dist > next){
                dist = next;
                xx = nx;
                yy = ny;
            }
        }
        int m = 1;
        if(dist == Math.abs(medusaX - x) + Math.abs(medusaY - y))m = 0;

        armyMap[x][y]--;
        armyMap[xx][yy]++;
        return new int[]{xx,yy, m};    
        
    }


    // 메두사와 가까운쪽으로 최대 2칸 이동
    // 1. 상하좌우, 2. 좌우상하
    // 메두사 시야로 이동 불가
    // 격자 밖 이동 불가
    public static int[] movingArmys(int medusaX, int medusaY){
        int score1 = 0;
        int score2 = 0;

        Iterator<Army> iterator = armys.iterator();
        while(iterator.hasNext()){
            Army army = iterator.next();
            if(!army.isRock){
                // 전사 이동 가능
                int[] cur1 = move(army.x, army.y ,0, medusaX, medusaY);
                score1 += cur1[2];
                if(medusaX == cur1[0] && medusaY == cur1[1]){
                    iterator.remove();
                    score2++;
                    armyMap[cur1[0]][cur1[1]]= 0;
                    continue;
                }
                army.x = cur1[0];
                army.y = cur1[1];
                
                int[] cur2 = move(army.x, army.y ,2, medusaX, medusaY);
                score1 += cur2[2];
                if(medusaX == cur2[0] && medusaY == cur2[1]){
                    iterator.remove();
                    score2++;
                    armyMap[cur2[0]][cur2[1]]= 0;
                    continue;
                }        
                army.x = cur2[0];
                army.y = cur2[1];
                
            }
        }
        return new int[]{score1, score2};
    }

    //  전사가 보는 시야 제외하기 (4 : 전사 시야, 1 : 메두사 시야 )
    public static void armySights(int medusaX, int medusaY, int d, int armyX, int armyY, int[][] completeRock){
        // 90도 시야
        for(int depth = 1;depth<n;depth++){
            if(d <= 1){  // 상하
                int start = armyY + depth * (-1);
                int end = armyY + depth;
                int x = armyX + depth * dx[d];
                if(armyY < medusaY){
                    end = armyY;
                }else if(armyY == medusaY){
                    start = armyY;
                    end = armyY;
                }else{
                    start = armyY;
                }
                for(int y=start;y<=end;y++){
                    if(x<0||y<0||x>=n||y>=n)continue;
                    completeRock[x][y] = 4;
                }
            }

            else{      // 좌우
                int start = armyX + depth * (-1);
                int end = armyX + depth;
                int y = armyY + depth * dy[d];
                if(armyX < medusaX){
                    end = armyX;
                }else if(armyX == medusaX){
                    start = armyX;
                    end = armyX;
                }else{
                    start = armyX;
                }
                for(int x=start;x<=end;x++){
                
                    if(x<0||y<0||x>=n||y>=n)continue;
                    completeRock[x][y] = 4;

                }

            }
        }
    }
    // 메두사 시야의 전사 돌로 만들기
    public static int medusaGaze(int medusaX, int medusaY){


        int score = 0;

        Queue<int[]> cadidateRocks = new ArrayDeque<>();
        rocks = new int[n][n];
        for(int d = 0;d<4;d++){  // 상하좌우
            int[][] completeRock = new int[n][n];
            Queue<int[]> tempCadidateRocks = new ArrayDeque<>();
            int tempScore = 0;

            // 90도 시야
            for(int depth = 1;depth<n;depth++){
                if(d <= 1){  // 상하
                    int start = medusaY + depth * (-1);
                    int end = medusaY + depth;
                    int x = medusaX + depth * dx[d];

                    for(int y=start;y<=end;y++){
                    
                        if(x<0||y<0||x>=n||y>=n|| completeRock[x][y] == 4)continue;
                        if(armyMap[x][y] > 0){  // 전사가 시야에 들어옴
                            tempCadidateRocks.add(new int[]{x,y});  // 예비 돌로 만들 전사
                            tempScore += armyMap[x][y];
                            armySights(medusaX, medusaY, d, x, y,completeRock); // 전사 시야
                        }
                        completeRock[x][y] = 1;
                    }
                }

                else{      // 좌우
                    int start = medusaX + depth * (-1);
                    int end = medusaX + depth;
                    int y = medusaY + depth * dy[d];
                    for(int x=start;x<=end;x++){

                        if(x<0||y<0||x>=n||y>=n || completeRock[x][y] == 4)continue;
                        if(armyMap[x][y] > 0){  // 전사가 시야에 들어옴
                            tempCadidateRocks.add(new int[]{x,y});  // 예비 돌로 만들 전사
                            tempScore += armyMap[x][y];
                            armySights(medusaX, medusaY, d, x, y,completeRock); // 전사 시야
                        }
                        completeRock[x][y] = 1;

                    }

                }
                
              
            }
            if(tempScore > score){
                score = tempScore;
                cadidateRocks = tempCadidateRocks;
                rocks = completeRock;
            }
        }

        // 전사 돌로 만들기 

        Iterator<Army> iterator = armys.iterator();        
        while(iterator.hasNext()){
            Army army = iterator.next();
            army.isRock = false;
        }

        while(!cadidateRocks.isEmpty()){
            int[] cur = cadidateRocks.poll();
            iterator = armys.iterator();        
            while(iterator.hasNext()){
                Army army = iterator.next();
                int x = army.x;
                int y = army.y;
                if(x == cur[0] && y == cur[1]){
                    army.isRock = true;
                }
            }
        }
        return score;

    }
    


    public static int[] movingMedusa(int x, int y){
        int nx = medusaRoute[x][y][0];
        int ny = medusaRoute[x][y][1];

        if(armyMap[nx][ny] > 0){
            
            // armyMap에서 전사 제거
            armyMap[nx][ny] = 0; 

            // 리스트에서 전사 제거
            Iterator<Army> iterator = armys.iterator();
            while(iterator.hasNext()){
                Army army = iterator.next();
                if(army.x == nx && army.y == ny){
                    iterator.remove();
                }
            }
            
        }
        return new int[]{nx, ny}; 
        
    }
    // 출발지부터 도착지까지 도로가 이어지는 판단 (medusaRoute에 저장)
    public static boolean isAbleDestination(int sx, int sy,int ex, int ey){

        int[] dx = new int[]{0,0,1,-1};  
        int[] dy = new int[]{1,-1,0,0};

        Queue<int[]> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[n][n];
        int[][] distance = new int[n][n];
        for(int i=0;i<n;i++){
            Arrays.fill(distance[i],Integer.MAX_VALUE);
        }
        medusaRoute = new int[n][n][2];


        q.offer(new int[]{ex,ey});
        visited[ex][ey] = true;
        distance[ex][ey] = 0;

        while(!q.isEmpty()){
            int[] cur = q.poll();
            int x = cur[0];
            int y = cur[1];
            if(x == sx && y == sy){

                return true;
            }
            for(int d=0;d<dx.length;d++){
                int nx = x + dx[d];
                int ny = y + dy[d];
                if(nx<0||ny<0||nx>=n||ny>=n || map[nx][ny] == 1 || visited[nx][ny]) continue;  // 도로가 아님
                if(distance[nx][ny] > distance[x][y] + 1){
                    distance[nx][ny] = distance[x][y] + 1;
                    q.offer(new int[]{nx, ny});
                    visited[nx][ny] = true;
                    medusaRoute[nx][ny] = new int[]{x,y};
                }  
                
            }
        }
        

        return false;
    }
}