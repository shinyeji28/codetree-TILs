import java.util.*;
import java.io.*;

public class Main {
    static final int SIZE = 5;
    static int[][] map = new int[SIZE][SIZE];
    static int[][] originMap = new int[SIZE][SIZE];
    static int[][] maxValueMap = new int[SIZE][SIZE];  // 최대 가치의 맵
    static boolean[][] visited;

    static List<Integer> result = new ArrayList<>();

    static Queue<Integer> newPiece = new ArrayDeque<>();
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(br.readLine());
        int k = Integer.parseInt(st.nextToken());  // 탐사 반복 횟수
        int m  = Integer.parseInt(st.nextToken());

        for(int i=0;i<SIZE;i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0;j<SIZE;j++){
                int input = Integer.parseInt(st.nextToken());
                map[i][j] = input;
                originMap[i][j] = input;
                maxValueMap[i][j] = input;
            }
        }

        st = new StringTokenizer(br.readLine());
        for(int i=0;i<m;i++){
            newPiece.offer(Integer.parseInt(st.nextToken()));
        }

        detect(k);
        for(Integer r : result){
            sb.append(r+" ");
        }
        System.out.println(sb);
    }

    public static void detect(int k){

        for(int kk=0;kk<k;kk++){
            int sum = 0; // 해당 턴의 가치 합
            int value = 0;  // 리턴 값
            value = detectAndGetValue();
            if(value==0)return;
            result.add(value);
        }
    }


    public static int detectAndGetValue(){
        int maxValue = 0;
        int r = 4;
        int x = 6;
        int y = 6;
        boolean isNew = false;

        // 탐색과 유물 획득
        // 중심 좌표 선정

        for(int i=1;i<SIZE-1;i++){
            for(int j=1;j<SIZE-1;j++){

                for(int rNum=0;rNum<3;rNum++){
                    copyMap(map, originMap); // 배열 복사 

                    rotation(i,j, rNum);
                    int v = getValue(map);   // 임시 유물 획득
                    if(v > maxValue){    // 1. 가치 큰것
                        isNew = true;
                    }else if(v == maxValue){  
                        if(rNum<r){        // 2. 회전 각도가 작은 것
                            isNew = true;
                        }else if(rNum==r){  // 3.중심 좌표 열이 작은 것 열이 같으면 행이 작은 것
                            if(j < y ){
                                isNew = true;
                            }else if(j == y && i < x){
                                isNew = true;
                            }
                        }
                    }
                    if(isNew){
                        maxValue = v;
                        r = rNum;
                        x = i;
                        y = j;
                        copyMap(maxValueMap, map);
                    }
                    isNew = false;
                }
            }
        }
        if(maxValue == 0)return 0;

        // 연쇄 유물 획득
        copyMap(originMap, maxValueMap);
        
        // 조각 채우기
        while(true){
            int re = fillThePieces(originMap);
            if(re==0)break;
            maxValue += re;

        }

        return maxValue;
    }
    public static int fillThePieces(int[][] m){
        // 채우기
        A: for(int j=0;j<SIZE;j++){
            for(int i=SIZE-1;i>=0;i--){
                if(m[i][j]==0){
                    if(newPiece.isEmpty())break A;
                    m[i][j] = newPiece.poll();
                }
            }
        }
        return getValue(m);  // 유물 획득
    }

    public static void copyMap(int[][] m1, int[][] m2){
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){   
                m1[i][j] = m2[i][j];
            }
        }
    }
    
    // 중심좌표를 기준으로 회전하기 90도
    public static void rotation(int x,int y, int rd){
        int[] dx1 = new int[]{-1,-1,1,1,-1}; 
        int[] dy1 = new int[]{-1,1,1,-1,-1};
        int[] dx2 = new int[]{-1,0,1,0,-1};
        int[] dy2 = new int[]{0,1,0,-1,0}; 
  
        for(int r = 0;r <=rd;r++){
            int prev = map[x + dx1[0]][y + dy1[0]];
            for(int i=0;i<5;i++){
                int temp = prev;
                prev = map[x + dx1[i]][y + dy1[i]];
                map[x + dx1[i]][y + dy1[i]] = temp; 
            }

            prev = map[x + dx2[0]][y + dy2[0]];
            for(int i=0;i<5;i++){
                int temp = prev;
                prev = map[x + dx2[i]][y + dy2[i]];
                map[x + dx2[i]][y + dy2[i]] = temp; 
            }
        }
  
        
    }
    // 유물 획득
    public static int getValue(int[][] m){

        visited = new boolean[SIZE][SIZE];
        int sum = 0;
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                if(visited[i][j]||m[i][j]==0)continue;
                sum += bfs(i,j, m); 
            }
        }
    
        return sum;  // 가치 반환
    }
    public static int bfs(int x, int y, int[][] m){
        int sum = 0;
        int[] dx = new int[]{0,0,1,-1};
        int[] dy = new int[]{1,-1,0,0};
        
        Queue<int[]> q = new ArrayDeque<>();
        Queue<int[]> pos = new ArrayDeque<>();

        q.offer(new int[]{x,y});
        visited[x][y] = true;
        pos.add(new int[]{x,y});
        int cnt = 1;

        while(!q.isEmpty()){
            int[] cur = q.poll();
            int cx = cur[0];
            int cy = cur[1];
            for(int d=0;d<4;d++){
                int nx = cx + dx[d];
                int ny = cy + dy[d];
                if(nx < 0 || ny < 0 || nx>=SIZE || ny>=SIZE || visited[nx][ny] ||m[nx][ny] != m[x][y])continue;
                q.offer(new int[]{nx,ny});
                visited[nx][ny] = true;
                pos.add(new int[]{nx,ny});
                cnt++;
            }
        }
        if(cnt>=3){
            while(!pos.isEmpty()){
                int[] cur = pos.poll();
                m[cur[0]][cur[1]] = 0;
            }
            return cnt;
        }
        
        return 0;
    }
}