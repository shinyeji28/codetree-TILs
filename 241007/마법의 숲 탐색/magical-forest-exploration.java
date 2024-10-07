/*

이동

남 -> 서 -> 동 
1. 갈 자리 체크
2. 갈 수 있다면 가기
3. 없다면 고렘 포인트 기준 회전

*/
import java.util.*;
import java.io.*;
public class Main {
    static int R,C;
    static int[][] map;
    static boolean[][] visited;
    static int result = 0;
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());
        map = new int[R+2][C];           // 1~k 골렘 존재, -1 탈출구
        visited = new boolean[R+2][C];
        for(int i=0;i<k;i++){
            st = new StringTokenizer(br.readLine());
            int ci = Integer.parseInt(st.nextToken())-1;
            int d = Integer.parseInt(st.nextToken());

            moving(ci, d, i+1);
        }
        System.out.println(result);
    }
    public static void moving(int c, int d, int k){ 
        // 내려가다가 막히면 서로 이동 후 내려가기
        // 서로 이동 후 내려가다가 막히면 동으로 이동 후 내려가기

        // 내릴 수 있는 위치
        int[] dx = new int[]{-1,0,1,0}; // 북 동 남 서
        int[] dy = new int[]{0,1,0,-1};

        // 다음 골렘의 좌표
        int[][] rx = new int[][]{
            {1,2,1},
            {-1,0,1},
            {-1,0,1},
        };
        int[][] ry = new int[][]{
            {-1,0,1},
            {1,2,1},
            {-1,-2,-1}
        };

        // 다음 중심 좌표
        int[] cx = new int[]{1,0,0};  // 남 서 동
        int[] cy = new int[]{0,1,-1};

        int x=0;
        int y=c;
        int maxCenter = x;
        for(int dire = 0;dire<3;dire++){  
            int i;
            A : while(true){
                for(i=0;i<3;i++){
                    int nx = x + rx[dire][i];
                    int ny = y + ry[dire][i];
                    if(nx<0||ny<0||nx>=R+2||ny>=C || map[nx][ny]!=0) break A;
                }
                if(i == 3){ // 이동 가능 중심 좌표 갱신
                    int[] pos = goDown(x + cx[dire], y + cy[dire]);
                    if(maxCenter < pos[0]){
                        maxCenter = pos[0];
                        
                        x = pos[0];
                        y = pos[1];
                        if(dire == 1){ // 서 왼쪽 회전
                            d = (d + 1) % 4;
                        }else if(dire == 2){  // 동 오른쪽 회전
                            d = (d + 3) % 4;
                        }
                    }else{
                        break A;
                    }
                    
                }
            }
            
        }
        // 골렘이 5p가 모두 <= 2 면 회전할 필요없이 초기화
        if(x <= 2){
            map = new int[R+2][C];
            return;
        }
        // 골렘이 더 이상 이동하지 못함
        for(int i=0;i<4;i++){
            map[x+dx[i]][y+dy[i]] = (d==i)? k*(-1) : k;
        }
        map[x][y] = k;
        working(x, y); // 정령을 최하단으로 이동
    }

    public static int[] goDown(int x, int y){
        int[] dx = new int[]{1,2,1};
        int[] dy = new int[]{-1,0,1};
        
        A : while(true){
            int i;
            for(i=0;i<3;i++){
                int nx = x + dx[i];
                int ny = y + dy[i];
                if(nx<0||ny<0||nx>=R+2||ny>=C || map[nx][ny]!=0) break A;
            }
            if(i == 3){ // 이동 가능 중심 좌표 갱신
                x = x + dx[1]-1;
                y = y + dy[1];
            }
        }

        return new int[]{x,y};
    }
    public static void working(int x, int y){  // 정렬 최하단 칸으로 이동
        
        Queue<int[]> q = new ArrayDeque<>();
        boolean[][] visited = new boolean[R+2][C];
        int r = x;

        int[] dx = new int[]{-1,0,1,0};
        int[] dy = new int[]{0,1,0,-1};

        q.offer(new int[]{x,y});
        visited[x][y] = true;

        while(!q.isEmpty()){
            int[] pos = q.poll();
            int cx = pos[0];
            int cy = pos[1];
            if(cx == R+2){
                r = cx;
                break;
            }
            r = Math.max(r, cx);
            for(int d=0;d<4;d++){
                int nx = cx + dx[d];
                int ny = cy + dy[d];
                if(nx<0||ny<0||nx>=R+2||ny>=C|| visited[nx][ny])continue;

                if(map[cx][cy]<0 || map[cx][cy] == map[nx][ny] || map[cx][cy] == map[nx][ny]* (-1)){
                    visited[nx][ny] = true;
                    q.offer(new int[]{nx,ny});
                }
            }
        }
       
        result += (r-1);
    }
}